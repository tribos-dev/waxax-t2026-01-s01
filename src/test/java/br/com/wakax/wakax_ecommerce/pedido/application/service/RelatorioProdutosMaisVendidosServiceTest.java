package br.com.wakax.wakax_ecommerce.pedido.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class RelatorioProdutosMaisVendidosServiceTest {

  @Mock private PedidoRepository pedidoRepository;
  @Mock private CarrinhoRepository carrinhoRepository;
  @Mock private ClienteService clienteService;

  @InjectMocks private PedidoApplicationService service;

  private LocalDateTime dataInicio;
  private LocalDateTime dataFim;
  private List<ProdutoMaisVendidoResponse> produtosMock;

  @BeforeEach
  void setUp() {
    dataInicio = LocalDateTime.now().minusDays(30);
    dataFim = LocalDateTime.now();
    produtosMock =
        List.of(
            new ProdutoMaisVendidoResponse(
                UUID.randomUUID(), "Produto A", 50L, new BigDecimal("500.00")),
            new ProdutoMaisVendidoResponse(
                UUID.randomUUID(), "Produto B", 30L, new BigDecimal("300.00")));
  }

  @Test
  void deveRetornarRelatorioDeProdutosMaisVendidos() {
    when(pedidoRepository.buscaProdutosMaisVendidos(
            eq(dataInicio), eq(dataFim), any(Pageable.class)))
        .thenReturn(produtosMock);

    List<ProdutoMaisVendidoResponse> resultado =
        service.geraRelatorioProdutosMaisVendidos(dataInicio, dataFim, 10);

    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals("Produto A", resultado.get(0).getDescricaoProduto());
    assertEquals(50L, resultado.get(0).getQuantidadeTotal());
    verify(pedidoRepository)
        .buscaProdutosMaisVendidos(eq(dataInicio), eq(dataFim), any(Pageable.class));
  }

  @Test
  void deveLancarExcecaoQuandoDataInicioForNula() {
    APIException ex =
        assertThrows(
            APIException.class, () -> service.geraRelatorioProdutosMaisVendidos(null, dataFim, 10));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.RELATORIO_DATA_OBRIGATORIA, ex.getErrorCode());
    verify(pedidoRepository, never()).buscaProdutosMaisVendidos(any(), any(), any());
  }

  @Test
  void deveLancarExcecaoQuandoDataFimForNula() {
    APIException ex =
        assertThrows(
            APIException.class,
            () -> service.geraRelatorioProdutosMaisVendidos(dataInicio, null, 10));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.RELATORIO_DATA_OBRIGATORIA, ex.getErrorCode());
    verify(pedidoRepository, never()).buscaProdutosMaisVendidos(any(), any(), any());
  }

  @Test
  void deveLancarExcecaoQuandoDataInicioForDepoisDeDataFim() {
    APIException ex =
        assertThrows(
            APIException.class,
            () -> service.geraRelatorioProdutosMaisVendidos(dataFim, dataInicio, 10));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.RELATORIO_DATA_INVALIDA, ex.getErrorCode());
    verify(pedidoRepository, never()).buscaProdutosMaisVendidos(any(), any(), any());
  }

  @Test
  void deveUsarLimiteDefaultQuandoLimiteForNulo() {
    when(pedidoRepository.buscaProdutosMaisVendidos(
            eq(dataInicio), eq(dataFim), any(Pageable.class)))
        .thenReturn(produtosMock);

    service.geraRelatorioProdutosMaisVendidos(dataInicio, dataFim, null);

    verify(pedidoRepository)
        .buscaProdutosMaisVendidos(
            eq(dataInicio), eq(dataFim), argThat(p -> p.getPageSize() == 10));
  }

  @Test
  void deveUsarLimiteMaximoQuandoLimiteUltrapassar100() {
    when(pedidoRepository.buscaProdutosMaisVendidos(
            eq(dataInicio), eq(dataFim), any(Pageable.class)))
        .thenReturn(produtosMock);

    service.geraRelatorioProdutosMaisVendidos(dataInicio, dataFim, 200);

    verify(pedidoRepository)
        .buscaProdutosMaisVendidos(
            eq(dataInicio), eq(dataFim), argThat(p -> p.getPageSize() == 100));
  }
}
