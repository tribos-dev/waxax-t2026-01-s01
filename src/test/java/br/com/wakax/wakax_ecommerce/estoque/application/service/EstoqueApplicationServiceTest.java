package br.com.wakax.wakax_ecommerce.estoque.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.estoque.api.request.AdicionaQuantidadeRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;

@ExtendWith(MockitoExtension.class)
class EstoqueApplicationServiceTest {

  @Mock private EstoqueRepository estoqueRepository;

  @InjectMocks private EstoqueApplicationService estoqueService;

  @Test
  void deveListarTodoEstoqueComSucesso() {
    List<Estoque> estoques = EstoqueDataHelper.listaEstoquePadrao();
    when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(estoques);
    EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
    assertNotNull(response);
    assertEquals(2, response.getItens().size());
    assertEquals("Mouse", response.getItens().get(0).getDescricaoProduto());
    assertEquals("Notebook", response.getItens().get(1).getDescricaoProduto());
    verify(estoqueRepository, times(1)).buscarComFiltro(null, null);
  }

  @Test
  void deveRetornarListaVaziaQuandoNaoHaEstoque() {
    when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(Collections.emptyList());
    EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
    assertNotNull(response);
    assertTrue(response.getItens().isEmpty());
    assertEquals(0, BigDecimal.ZERO.compareTo(response.getValorTotalInventario()));
    verify(estoqueRepository).buscarComFiltro(null, null);
  }

  @Test
  void deveCalcularValorTotalDoInventario() {
    List<Estoque> estoques = EstoqueDataHelper.listaEstoquePadrao();
    when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(estoques);
    EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
    assertEquals(0, new BigDecimal("90").compareTo(response.getValorTotalInventario()));
    verify(estoqueRepository).buscarComFiltro(null, null);
  }

  @Test
  void deveExecutarBaixaDeEstoque() {
    var estoque = EstoqueDataHelper.createEstoque(20, "10.00", "200.00");
    var request = EstoqueDataHelper.createRequest(5);

    when(estoqueRepository.buscaEstoquePorIdProduto(any())).thenReturn(Optional.of(estoque));

    estoqueService.removeQuantidadeEstoque(
        UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), request);

    verify(estoqueRepository, times(1)).salva(estoque);
    assertEquals(15, estoque.getQuantidadeDisponivel());
  }

  @Test
  void deveZerarEstoqueECustosCompletamente() {
    var estoque = EstoqueDataHelper.createEstoque(15, "10.00", "150.00");
    var request = EstoqueDataHelper.createRequest(15);

    when(estoqueRepository.buscaEstoquePorIdProduto(any())).thenReturn(Optional.of(estoque));

    estoqueService.removeQuantidadeEstoque(
        UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), request);

    assertEquals(0, estoque.getQuantidadeDisponivel());
    assertEquals(BigDecimal.ZERO, estoque.getCustoTotal());
    verify(estoqueRepository).salva(estoque);
  }

  @Test
  void deveFalharPorQuantidadeInsuficiente() {
    var estoque = EstoqueDataHelper.createEstoque(10, "10.00", "100.00");
    var request = EstoqueDataHelper.createRequest(15);

    when(estoqueRepository.buscaEstoquePorIdProduto(any())).thenReturn(Optional.of(estoque));

    assertThrows(
        APIException.class,
        () ->
            estoqueService.removeQuantidadeEstoque(
                UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"), request));

    verify(estoqueRepository, never()).salva(any()); // Garante consistência: não salvou!
    assertEquals(10, estoque.getQuantidadeDisponivel());
  }

  @Test
    void DeveAdicionarQuantidadeComRecalculoComSucesso(){
//      Produto produto = EstoqueDataHelper.criarProdutoComPreco("Toque de seda, extra macio, Cinza", BigDecimal.valueOf(359));
      Estoque estoque = EstoqueDataHelper.createEstoque(30, "70", "2100" );
      UUID idProduto = estoque.getProduto().getId();
      AdicionaQuantidadeRequest request = EstoqueDataHelper.criaRequest();

      when(estoqueRepository.buscaEstoquePorIdProduto(idProduto)).thenReturn(Optional.of(estoque));

      EstoqueResponse response = estoqueService.adicionaQuantidade(idProduto, request);

      assertEquals(40, response.getQuantidadeDisponivel());
      assertEquals(0, new BigDecimal("67.50").compareTo(response.getCustoMedio()));
      assertEquals(0, new BigDecimal("2700.00").compareTo(response.getCustoTotal()));

      verify(estoqueRepository, times(1)).salva(estoque);

  }
}
