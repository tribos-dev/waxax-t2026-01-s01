package br.com.wakax.wakax_ecommerce.carrinho.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;
import br.com.wakax.wakax_ecommerce.carrinho.application.factory.ProcessadorEstoqueFactory;
import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.application.strategy.ProcessadorEstoque;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.StatusCarrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.produto.application.repository.ProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

@ExtendWith(MockitoExtension.class)
class CarrinhoApplicationServiceTest {

  @InjectMocks CarrinhoApplicationService applicationService;

  @Mock CarrinhoRepository carrinhoRepository;

  @Mock ProdutoRepository produtoRepository;

  @Mock ClienteRepository clienteRepository;

  @Mock ProcessadorEstoqueFactory processadorEstoqueFactory;

  @Mock ProcessadorEstoque processadorEstoque;

  @Test
  void deveAdicionarItemAUmCarrinhoAtivo() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoVazio(cliente);
    Produto produto = CarrinhoDataHelper.criaProduto();
    ItemCarrinhoRequest itemCarrinhoRequest =
        CarrinhoDataHelper.criaItemCarrinhoRequest(produto.getId());

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscaCarrinhoAtivoDoCliente(cliente.getId()))
        .thenReturn(Optional.of(carrinho));
    when(produtoRepository.buscaProdutoPorId(produto.getId())).thenReturn(produto);
    when(carrinhoRepository.salva(carrinho)).thenReturn(carrinho);
    when(processadorEstoqueFactory.obterProcessador()).thenReturn(processadorEstoque);
    doNothing().when(processadorEstoque).aoAdicionarItem(any(Produto.class), anyInt());

    CarrinhoResponse carrinhoComItem =
        applicationService.adicionaItemNoCarrinho(cliente.getId(), itemCarrinhoRequest);

    assertEquals(carrinho.getId(), carrinhoComItem.getIdCarrinho());
    assertEquals(1, carrinhoComItem.getItensCarrinho().size());
    assertEquals(2, carrinhoComItem.getItensCarrinho().get(0).getQuantidade());

    verify(carrinhoRepository, times(1)).buscaCarrinhoAtivoDoCliente(cliente.getId());
    verify(produtoRepository, times(1)).buscaProdutoPorId(produto.getId());
    verify(carrinhoRepository, times(1)).salva(carrinho);
  }

  @Test
  void deveCriarNovoCarrinhoSeNaoExistirCarrinhoAtivo() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoVazio(cliente);
    Produto produto = CarrinhoDataHelper.criaProduto();
    ItemCarrinhoRequest itemCarrinhoRequest =
        CarrinhoDataHelper.criaItemCarrinhoRequest(produto.getId());

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscaCarrinhoAtivoDoCliente(cliente.getId()))
        .thenReturn(Optional.empty());
    when(produtoRepository.buscaProdutoPorId(produto.getId())).thenReturn(produto);
    when(carrinhoRepository.salva(any(Carrinho.class))).thenReturn(carrinho);
    when(processadorEstoqueFactory.obterProcessador()).thenReturn(processadorEstoque);
    doNothing().when(processadorEstoque).aoAdicionarItem(any(Produto.class), anyInt());

    applicationService.adicionaItemNoCarrinho(cliente.getId(), itemCarrinhoRequest);

    verify(carrinhoRepository, times(1)).buscaCarrinhoAtivoDoCliente(cliente.getId());
    verify(produtoRepository, times(1)).buscaProdutoPorId(produto.getId());
    verify(carrinhoRepository, times(1)).salva(any(Carrinho.class));
  }

  @Test
  void deveRetornarCarrinhoPorId() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscaCarrinhoPorId(carrinho.getId())).thenReturn(carrinho);

    CarrinhoResponse carrinhoBuscado =
        applicationService.buscaCarrinhoPorId(cliente.getId(), carrinho.getId());

    assertEquals(carrinho.getId(), carrinhoBuscado.getIdCarrinho());
    verify(clienteRepository, times(1)).buscaClientePorId(cliente.getId());
    verify(carrinhoRepository, times(1)).buscaCarrinhoPorId(carrinho.getId());
  }

  @Test
  void deveRetornarTodosOsCarrinhosDeUmCliente() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);
    Carrinho carrinho1 = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);
    List<Carrinho> carrinhos = List.of(carrinho, carrinho1);

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscarTodosOsCarrinhos(cliente.getId())).thenReturn(carrinhos);

    List<CarrinhosListAllResponse> carrinhosBuscados =
        applicationService.buscarTodosOsCarrinhos(cliente.getId());

    assertEquals(2, carrinhosBuscados.size());
    assertEquals(carrinho.getId(), carrinhosBuscados.get(0).getIdCarrinho());
    assertEquals(carrinho1.getId(), carrinhosBuscados.get(1).getIdCarrinho());

    verify(clienteRepository, times(1)).buscaClientePorId(cliente.getId());
    verify(carrinhoRepository, times(1)).buscarTodosOsCarrinhos(cliente.getId());
  }

  @Test
  void deveRetornarListaVaziaQuandoNaoHouverCarrinhos() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscarTodosOsCarrinhos(cliente.getId())).thenReturn(List.of());

    List<CarrinhosListAllResponse> carrinhosBuscados =
        applicationService.buscarTodosOsCarrinhos(cliente.getId());

    assertNotNull(carrinhosBuscados);
    assertTrue(carrinhosBuscados.isEmpty());

    verify(clienteRepository, times(1)).buscaClientePorId(cliente.getId());
    verify(carrinhoRepository, times(1)).buscarTodosOsCarrinhos(cliente.getId());
  }

  @Test
  void deveRetornarTodosOsCarrinhosAtivosEFinalizados() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    Carrinho carrinho = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);
    Carrinho carrinho1 = CarrinhoDataHelper.criaCarrinhoFinalizadoComUmItem(cliente);
    List<Carrinho> carrinhos = List.of(carrinho, carrinho1);

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscarTodosOsCarrinhos(cliente.getId())).thenReturn(carrinhos);

    List<CarrinhosListAllResponse> carrinhosBuscados =
        applicationService.buscarTodosOsCarrinhos(cliente.getId());

    assertEquals(2, carrinhosBuscados.size());
    assertEquals(StatusCarrinho.ATIVO, carrinhosBuscados.get(0).getStatusCarrinho());
    assertEquals(StatusCarrinho.FINALIZADO, carrinhosBuscados.get(1).getStatusCarrinho());

    verify(clienteRepository, times(1)).buscaClientePorId(cliente.getId());
    verify(carrinhoRepository, times(1)).buscarTodosOsCarrinhos(cliente.getId());
  }

  @Test
  void deveRetornarTodosOsCarrinhosOrdenadosPelaData() {
    Cliente cliente = CarrinhoDataHelper.criaCliente();
    LocalDateTime base = LocalDateTime.now();

    Carrinho carrinhoRecente = CarrinhoDataHelper.criaCarrinhoAtivoComUmItem(cliente);
    Carrinho carrinhoAntigo = CarrinhoDataHelper.criaCarrinhoFinalizadoDeOntem(cliente);

    List<Carrinho> carrinhos = List.of(carrinhoAntigo, carrinhoRecente);

    when(clienteRepository.buscaClientePorId(cliente.getId())).thenReturn(cliente);
    when(carrinhoRepository.buscarTodosOsCarrinhos(cliente.getId())).thenReturn(carrinhos);

    List<CarrinhosListAllResponse> resposta =
        applicationService.buscarTodosOsCarrinhos(cliente.getId());

    assertEquals(2, resposta.size());

    assertEquals(carrinhoRecente.getId(), resposta.get(0).getIdCarrinho());
    assertEquals(carrinhoAntigo.getId(), resposta.get(1).getIdCarrinho());

    verify(clienteRepository).buscaClientePorId(cliente.getId());
    verify(carrinhoRepository).buscarTodosOsCarrinhos(cliente.getId());
  }
}
