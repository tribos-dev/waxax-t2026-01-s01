package br.com.wakax.wakax_ecommerce.estoque.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wakax.wakax_ecommerce.estoque.api.request.AdicionaQuantidadeRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.request.EstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.request.RemoveEstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.produto.application.repository.ProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class EstoqueApplicationService implements EstoqueService {
  private final EstoqueRepository estoqueRepository;
  private final ProdutoRepository produtoRepository;

  @Override
  @Transactional
  public EstoqueResponse criaEstoque(UUID idProduto, EstoqueRequest request) {
    log.info("[start] EstoqueApplicationService - criaEstoque");
    Produto produto = produtoRepository.buscaProdutoPorId(idProduto);
    validaSeJaExisteEstoque(produto.getId());
    Estoque estoque = new Estoque(request, produto);
    estoqueRepository.salva(estoque);
    log.info("[finish] EstoqueApplicationService - criaEstoque");
    return new EstoqueResponse(estoque);
  }

  @Override
  @Transactional(readOnly = true)
  public EstoqueResponse buscaEstoquePorIdProduto(UUID idProduto) {
    log.info("[start] EstoqueApplicationService - buscaEstoquePorIdProduto");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    log.info("[finish] EstoqueApplicationService - buscaEstoquePorIdProduto");
    return new EstoqueResponse(estoque);
  }

  private Estoque buscaEstoqueExistente(UUID idProduto) {
    return estoqueRepository
        .buscaEstoquePorIdProduto(idProduto)
        .orElseThrow(
            () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.ESTOQUE_NAO_ENCONTRADO));
  }

  @Override
  @Transactional(readOnly = true)
  public boolean temQuantidadeDisponivel(UUID idProduto, Integer quantidade) {
    log.info("[start] EstoqueApplicationService - temQuantidadeDisponivel");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    boolean disponivel = estoque.temQuantidadeDisponivel(quantidade);
    log.info("[finish] EstoqueApplicationService - temQuantidadeDisponivel");
    return disponivel;
  }

  @Override
  @Transactional
  public void reservaQuantidade(UUID idProduto, Integer quantidade) {
    log.info("[start] EstoqueApplicationService - reservaQuantidade");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    estoque.reservaQuantidade(quantidade);
    estoqueRepository.salva(estoque);
    log.info("[finish] EstoqueApplicationService - reservaQuantidade");
  }

  @Override
  @Transactional
  public void liberaReserva(UUID idProduto, Integer quantidade) {
    log.info("[start] EstoqueApplicationService - liberaReserva");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    estoque.liberaReserva(quantidade);
    estoqueRepository.salva(estoque);
    log.info("[finish] EstoqueApplicationService - liberaReserva");
  }

  @Override
  @Transactional
  public void liberaReservaDePedido(List<ItemPedido> itensPedido) {
    log.debug("[start] EstoqueApplicationService - liberaReservaDePedido");
    itensPedido.forEach(
        item -> {
          Estoque estoque = buscaEstoqueExistente(item.getProduto().getId());
          estoque.liberaReserva(item.getQuantidade());
          estoqueRepository.salva(estoque);
        });
    log.debug("[finish] EstoqueApplicationService - liberaReservaDePedido");
  }

  @Override
  @Transactional(readOnly = true)
  public EstoqueListagemResponse listarTodoEstoque(
      Integer quantidadeMinima, Boolean apenasEmFalta) {
    log.info("[start] listarTodoEstoque");
    List<Estoque> estoques = estoqueRepository.buscarComFiltro(quantidadeMinima, apenasEmFalta);
    log.info("[finish] listarTodoEstoque - Total: {}", estoques.size());
    return EstoqueListagemResponse.of(estoques);
  }

  @Transactional
  @Override
  public EstoqueResponse adicionaQuantidade(UUID idProduto, AdicionaQuantidadeRequest request) {
    log.info("[start] EstoqueApplicationService - adicionaEstoque");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    estoque.adicionaQuantidade(request.getQuantidade(), request.getCustoUnitario());
    estoque.validaQuantidadeMenorZero(request.getQuantidade());
    estoqueRepository.salva(estoque);
    log.debug("[finish] EstoqueApplicationService - adicionaEstoque");
    return new EstoqueResponse(estoque);
  }

  @Override
  @Transactional
  public void removeQuantidadeEstoque(UUID idProduto, RemoveEstoqueRequest request) {
    log.info("[start] EstoqueApplicationService - removeQuantidadeEstoque");
    Estoque estoque = buscaEstoqueExistente(idProduto);
    estoque.removeQuantidade(request.quantidade());
    estoqueRepository.salva(estoque);
    log.debug("[finish] EstoqueApplicationService - removeQuantidadeEstoque");
  }

  private void validaSeJaExisteEstoque(UUID idProduto) {
    estoqueRepository
        .buscaEstoquePorIdProduto(idProduto)
        .ifPresent(
            estoque -> {
              throw new APIException(HttpStatus.CONFLICT, ErrorCode.ESTOQUE_JA_EXISTE);
            });
  }
}
