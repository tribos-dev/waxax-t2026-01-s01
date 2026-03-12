package br.com.wakax.wakax_ecommerce.pagamento.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.PagamentoController;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.service.PagamentoDataHelper;
import br.com.wakax.wakax_ecommerce.pagamento.application.service.PagamentoService;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

  @Mock private PagamentoService pagamentoService;

  @InjectMocks private PagamentoController pagamentoController;

  private PagamentoRequest pagamentoRequest;
  private PagamentoResponse pagamentoResponse;
  private PagamentoPageResponse pagamentoPageResponse;
  private UUID pedidoId;
  private UUID pagamentoId;

  @BeforeEach
  void setUp() {
    pedidoId = UUID.randomUUID();
    pagamentoId = UUID.randomUUID();

    pagamentoRequest = PagamentoDataHelper.criaPagamentoRequestValido(pedidoId);

    Pedido pedido = PagamentoDataHelper.criaPedidoValido();
    Pagamento pagamento = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamento.setId(pagamentoId);

    pagamentoResponse = new PagamentoResponse(pagamento);
  }

  @Test
  void deveCriarPagamentoComSucesso() {
    when(pagamentoService.processaPagamento(pagamentoRequest)).thenReturn(pagamentoResponse);

    PagamentoResponse response = pagamentoController.processaPagamento(pagamentoRequest);

    assertNotNull(response);
    assertEquals(pagamentoResponse.getIdPagamento(), response.getIdPagamento());
    assertEquals(pagamentoResponse.getPedidoId(), response.getPedidoId());
    assertEquals(pagamentoResponse.getStatusPagamento(), response.getStatusPagamento());

    verify(pagamentoService).processaPagamento(pagamentoRequest);
  }

  @Test
  void deveBuscarPagamentoPorIdComSucesso() {
    when(pagamentoService.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamentoResponse);

    PagamentoResponse response = pagamentoController.buscaPagamentoPorId(pagamentoId);

    assertNotNull(response);
    assertEquals(pagamentoResponse.getIdPagamento(), response.getIdPagamento());
    assertEquals(pagamentoResponse.getPedidoId(), response.getPedidoId());

    verify(pagamentoService).buscaPagamentoPorId(pagamentoId);
  }

  @Test
  void deveRepassarExcecaoDoService() {
    RuntimeException exception = new RuntimeException("Erro no service");
    when(pagamentoService.processaPagamento(pagamentoRequest)).thenThrow(exception);

    assertThrows(
        RuntimeException.class, () -> pagamentoController.processaPagamento(pagamentoRequest));

    verify(pagamentoService).processaPagamento(pagamentoRequest);
  }

  @Test
  void deveRepassarExcecaoAoBuscarPorId() {
    RuntimeException exception = new RuntimeException("Pagamento não encontrado");
    when(pagamentoService.buscaPagamentoPorId(pagamentoId)).thenThrow(exception);

    assertThrows(
        RuntimeException.class, () -> pagamentoController.buscaPagamentoPorId(pagamentoId));

    verify(pagamentoService).buscaPagamentoPorId(pagamentoId);
  }

  @Test
  void deveListarPagamentosSemFiltrosComPaginacao() {
    StatusPagamento statusPagamento = null;
    int page = 0;
    int size = 10;
    PagamentoPageResponse pagamentoPageResponse = mock(PagamentoPageResponse.class);

    when(pagamentoService.buscaPagamentosPaginado(statusPagamento, page, size))
        .thenReturn(pagamentoPageResponse);

    PagamentoPageResponse resultado =
        pagamentoController.buscaPagamentosPaginado(statusPagamento, page, size);

    assertNotNull(resultado);
    assertEquals(pagamentoPageResponse, resultado);
    verify(pagamentoService, times(1)).buscaPagamentosPaginado(statusPagamento, page, size);
  }

  @Test
  void deveFiltrarPagamentosPorStatusPAGO() {
    StatusPagamento statusPagamento = StatusPagamento.PAGO;
    int page = 0;
    int size = 10;
    PagamentoPageResponse pagamentoPageResponse = mock(PagamentoPageResponse.class);

    when(pagamentoService.buscaPagamentosPaginado(statusPagamento, page, size))
        .thenReturn(pagamentoPageResponse);

    PagamentoPageResponse resultado =
        pagamentoController.buscaPagamentosPaginado(statusPagamento, page, size);

    assertNotNull(resultado);
    assertEquals(pagamentoPageResponse, resultado);
    verify(pagamentoService, times(1)).buscaPagamentosPaginado(statusPagamento, page, size);
  }

  @Test
  void deveBuscarPagamentoPorIdPedidoComSucesso() {
    var pagamentoPedidoResponse =
        mock(
            br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPedidoResponse
                .class);
    when(pagamentoService.buscaPagamentoPorIdPedido(pedidoId)).thenReturn(pagamentoPedidoResponse);

    var response = pagamentoController.buscaPagamentoPorIdPedido(pedidoId);

    assertNotNull(response);
    verify(pagamentoService).buscaPagamentoPorIdPedido(pedidoId);
  }

  @Test
  void deveReprocessarPagamentoComSucesso() {
    var response =
        mock(
            br.com.wakax.wakax_ecommerce.pagamento.application.api.response
                .ReprocessarPagamentoResponse.class);

    when(pagamentoService.reprocessaPagamento(pagamentoId)).thenReturn(response);

    var resultado = pagamentoController.reprocessaPagamento(pagamentoId);

    assertNotNull(resultado);
    assertEquals(response, resultado);

    verify(pagamentoService).reprocessaPagamento(pagamentoId);
  }
}
