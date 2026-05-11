package br.com.wakax.wakax_ecommerce.carrinho.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wakax.wakax_ecommerce.auth.security.service.TokenService;
import br.com.wakax.wakax_ecommerce.carrinho.api.request.AlteraQuantidadeDeItemRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;
import br.com.wakax.wakax_ecommerce.carrinho.application.factory.ProcessadorEstoqueFactory;
import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.application.strategy.ProcessadorEstoque;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.ItemCarrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueService;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.produto.application.repository.ProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import br.com.wakax.wakax_ecommerce.produto.domain.StatusProduto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CarrinhoApplicationService implements CarrinhoService {

  private final CarrinhoRepository carrinhoRepository;
  private final ProdutoRepository produtoRepository;
  private final ClienteRepository clienteRepository;
  private final ProcessadorEstoqueFactory processadorEstoqueFactory;
  private final TokenService tokenService;
  private final EstoqueService estoqueService;

  @Override
  @Transactional
  public CarrinhoResponse adicionaItemNoCarrinho(
      UUID idCliente, ItemCarrinhoRequest itemCarrinhoRequest) {
    log.info("[start] CarrinhoApplicationService - adicionaItemNoCarrinho");
    Cliente cliente = clienteRepository.buscaClientePorId(idCliente);
    Carrinho carrinho = buscaCarrinhoAtivoDoClienteOuCria(cliente);
    Produto produto = produtoRepository.buscaProdutoPorId(itemCarrinhoRequest.getIdProduto());
    if (produto.getStatus() == StatusProduto.INATIVO) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.PRODUTO_INDISPONIVEL);
    }

    ProcessadorEstoque processadorEstoque = processadorEstoqueFactory.obterProcessador();
    processadorEstoque.aoAdicionarItem(produto, itemCarrinhoRequest.getQuantidade());

    carrinho.adicionaItemAoCarrinho(itemCarrinhoRequest, produto);
    carrinhoRepository.salva(carrinho);
    log.debug("[finish] CarrinhoApplicationService - adicionaItemNoCarrinho");
    return new CarrinhoResponse(carrinho);
  }

  private Carrinho buscaCarrinhoAtivoDoClienteOuCria(Cliente cliente) {
    return carrinhoRepository
        .buscaCarrinhoAtivoDoCliente(cliente.getId())
        .orElseGet(() -> new Carrinho(cliente));
  }

  @Override
  @Transactional(readOnly = true)
  public CarrinhoResponse buscaCarrinhoPorId(UUID idCliente, UUID idCarrinho) {
    log.info("[start] CarrinhoApplicationService - buscaCarrinhoPorId");
    clienteRepository.buscaClientePorId(idCliente);
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(idCarrinho);
    log.debug("[finish] CarrinhoApplicationService - buscaCarrinhoPorId");
    return new CarrinhoResponse(carrinho);
  }

  @Override
  public List<CarrinhosListAllResponse> buscarTodosOsCarrinhos(UUID idCliente) {
    log.info("[start] CarrinhoApplicationService - buscarTodosOsCarrinhos");
    clienteRepository.buscaClientePorId(idCliente);
    List<Carrinho> carrinho = carrinhoRepository.buscarTodosOsCarrinhos(idCliente);
    List<CarrinhosListAllResponse> list =
        carrinho.stream().map(CarrinhosListAllResponse::new).toList();
    log.debug("[finish] CarrinhoApplicationService - buscarTodosOsCarrinhos");
    return list;
  }

  @Override
  @Transactional
  public void deletaItemDoCarrinho(String emailUsuario, UUID idCarrinho, UUID idItem) {

    log.info("[start] CarrinhoApplicationService - deletaItemDoCarrinho");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(idCarrinho);
    carrinho.removeItem(idItem, emailUsuario);
    carrinhoRepository.salva(carrinho);
    log.info("[finish] CarrinhoApplicationService - deletaItemDoCarrinho");
  }

  @Override
  public void restaurarCarrinho(UUID id) {
    log.info("[start] CarrinhoApplicationService - restaurarCarrinho");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoAtivoDoCliente(id).orElse(null);
    if (carrinho != null) {
      carrinho.ativar();
      carrinhoRepository.salva(carrinho);
    }
    log.debug("[finish] CarrinhoApplicationService - restaurarCarrinho");
  }

  @Override
  @Transactional
  public void alteraQuantidadeDeItem(
      UUID idCarrinho, UUID idItem, UUID idCliente, AlteraQuantidadeDeItemRequest request) {
    log.info("[start] CarrinhoApplicationService - alteraQuantidadeDeItem");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(idCarrinho);
    ItemCarrinho itemCarrinho = carrinho.buscaItemPorId(idItem);
    carrinho.validaSeCarrinhoEstaAptoAModificacoes(idCliente, idItem);
    Estoque estoque = buscaEstoqueDoProduto(itemCarrinho);
    carrinho.validaQuantidade(request.getQuantidade(), estoque);
    itemCarrinho.novaQuantidadeTotal(request.getQuantidade());
    log.debug("[finish] CarrinhoApplicationService - alteraQuantidadeDeItem");
  }

  @Override
  public Estoque buscaEstoqueDoProduto(ItemCarrinho itemCarrinho) {
    EstoqueResponse response =
        estoqueService.buscaEstoquePorIdProduto(itemCarrinho.getProduto().getId());
    return Estoque.fromResponse(response);
  }
}
