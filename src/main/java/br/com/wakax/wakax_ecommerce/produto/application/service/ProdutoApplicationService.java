package br.com.wakax.wakax_ecommerce.produto.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wakax.wakax_ecommerce.auth.credencial.domain.Credencial;
import br.com.wakax.wakax_ecommerce.auth.usuario.domain.Usuario;
import br.com.wakax.wakax_ecommerce.produto.api.ProdutoAlteraStatusRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoAtualizaResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListagemResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoResponse;
import br.com.wakax.wakax_ecommerce.produto.application.repository.HistoricoAtualizacaoProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.application.repository.ProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.HistoricoAtualizacaoProduto;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProdutoApplicationService implements ProdutoService {
  private final ProdutoRepository produtoRepository;
  private final HistoricoAtualizacaoProdutoRepository historicoAtualizacaoProdutoRepository;

  @Override
  public ProdutoResponse cadastraProduto(ProdutoRequest novoProduto) {
    log.debug("[start] ProdutoApplicationService - cadastraProduto");
    Produto produto = new Produto(novoProduto);
    produtoRepository.salva(produto);
    log.debug("[finish] ProdutoApplicationService - cadastraProduto");
    return new ProdutoResponse(produto);
  }

  @Override
  public ProdutoListResponse buscaProdutoPorId(UUID idProduto) {
    log.debug("[start] ProdutoApplicationService - buscaProdutoPorId");
    Produto produto = produtoRepository.buscaProdutoPorId(idProduto);
    log.debug("[finish] ProdutoApplicationService - buscaProdutoPorId");
    return new ProdutoListResponse(produto);
  }

  @Override
  public ProdutoListagemResponse listarTodosProdutos(int page, int size) {
    log.debug("[start] ProdutoApplicationService - listarTodosProdutos");
    Pageable pageable = PageRequest.of(page, size, Sort.by("descricao"));
    Page<Produto> produtos = produtoRepository.listarTodosProdutosPaginado(pageable);
    log.debug("[finish] ProdutoApplicationService - listarTodosProdutos");
    return ProdutoListagemResponse.convertePaginado(
        produtos.getContent(), produtos.getTotalElements());
  }

  @Override
  @Transactional
  public ProdutoAtualizaResponse atualizaProduto(
      UUID idProduto, ProdutoAtualizaRequest atualizaRequest) {
    log.debug("[start] ProdutoApplicationService - atualizaProduto");
    Produto produto = produtoRepository.buscaProdutoPorId(idProduto);
    produto.atualiza(atualizaRequest);
    produtoRepository.salva(produto);
    HistoricoAtualizacaoProduto historico =
        HistoricoAtualizacaoProduto.builder()
            .produto(produto)
            .usuario(buscaUsuarioLogado())
            .dataHora(LocalDateTime.now())
            .build();
    historicoAtualizacaoProdutoRepository.salva(historico);
    log.debug("[finish] ProdutoApplicationService - atualizaProduto");
    return new ProdutoAtualizaResponse(produto);
  }

  public Usuario buscaUsuarioLogado() {
    Credencial credencial =
        (Credencial) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return credencial.getUser();
  }

  @Override
  @Transactional
  public void removerProduto(UUID idProduto) {
    log.debug("[start] ProdutoApplicationService - removerProduto");
    Produto produto = produtoRepository.buscaProdutoPorId(idProduto);
    produto.inativa();
    produtoRepository.salva(produto);
    log.debug("[finish] ProdutoApplicationService - removerProduto");
  }

  @Override
  public void alteraStatusProduto(UUID idProduto, ProdutoAlteraStatusRequest statusRequest) {
    log.debug("[start] ProdutoApplicationService - alteraStatusProduto");
    Produto produto = produtoRepository.buscaProdutoPorId(idProduto);
    produto.alteraStatus(statusRequest.getStatus(), statusRequest.getMotivo());
    produtoRepository.salva(produto);
    log.debug("[finish] ProdutoApplicationService - alteraStatusProduto");
  }
}
