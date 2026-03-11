package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.factory.ProcessadorPagamentoFactory;
import br.com.wakax.wakax_ecommerce.pagamento.application.repository.PagamentoRepository;
import br.com.wakax.wakax_ecommerce.pagamento.application.strategy.PagamentoAguardandoStrategy;
import br.com.wakax.wakax_ecommerce.pagamento.application.strategy.PagamentoImediatoStrategy;
import br.com.wakax.wakax_ecommerce.pagamento.application.strategy.ProcessadorPagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

@ExtendWith(MockitoExtension.class)
class PagamentoApplicationServiceTest {

  @Mock private PagamentoRepository pagamentoRepository;
  @Mock private PedidoRepository pedidoRepository;
  @Mock private ProcessadorPagamentoFactory processadorFactory;
  @Mock private ProcessadorPagamento processadorPagamento;
  @Mock private PagamentoImediatoStrategy pagamentoImediatoStrategy;
  @Mock private PagamentoAguardandoStrategy pagamentoAguardandoStrategy;

  @InjectMocks private PagamentoApplicationService pagamentoApplicationService;
  @InjectMocks private ProcessadorPagamentoFactory realProcessadorFactory;

  private PagamentoRequest pagamentoRequest;
  private Pedido pedido;
  private Pagamento pagamento;
  private UUID pedidoId;
  private UUID pagamentoId;
  private CancelaPagamentoRequest cancelaPagamentoRequest;

  @BeforeEach
  void setUp() {
    pedidoId = UUID.randomUUID();
    pagamentoId = UUID.randomUUID();

    pagamentoRequest = PagamentoDataHelper.criaPagamentoRequestValido(pedidoId);
    pedido = PagamentoDataHelper.criaPedidoValido();
    pagamento = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamento.setId(pagamentoId);
    cancelaPagamentoRequest =
        PagamentoDataHelper.criaCancelaPagamentoRequest(
            pagamentoId, CancelaPagamentoRequest.builder().build());
  }

  @Test
  void deveCriarPagamentoComSucesso() {
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(pedido.getFormaPagamento()))
        .thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class)))
        .thenAnswer(
            invocation -> {
              Pagamento p = invocation.getArgument(0);
              p.setId(pagamentoId);
              return p;
            });

    PagamentoResponse response = pagamentoApplicationService.processaPagamento(pagamentoRequest);

    assertNotNull(response);
    assertEquals(pagamentoId, response.getIdPagamento());
    assertEquals(pedido.getId(), response.getPedidoId());
    assertEquals(StatusPagamento.AGUARDANDO, response.getStatusPagamento());

    verify(pagamentoRepository).buscaPagamentoPorPedidoId(pedidoId);
    verify(pedidoRepository).buscaPedidoPorId(pedidoId);
    verify(processadorFactory).obterProcessador(pedido.getFormaPagamento());
    verify(pagamentoRepository).salva(any(Pagamento.class));
    verify(pedidoRepository).salva(pedido);
  }

  @Test
  void deveLancarExcecaoQuandoPedidoJaPossuiPagamento() {
    Pagamento pagamentoExistente = PagamentoDataHelper.criaPagamentoValido(pedido);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId))
        .thenReturn(Optional.of(pagamentoExistente));

    APIException exception =
        assertThrows(
            APIException.class,
            () -> pagamentoApplicationService.processaPagamento(pagamentoRequest));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    assertEquals(ErrorCode.PEDIDO_JA_POSSUI_PAGAMENTO, exception.getErrorCode());

    verify(pagamentoRepository).buscaPagamentoPorPedidoId(pedidoId);
    verify(pedidoRepository, never()).buscaPedidoPorId(any());
    verify(pagamentoRepository, never()).salva(any());
  }

  @Test
  void deveBuscarPagamentoPorIdComSucesso() {
    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);

    PagamentoResponse response = pagamentoApplicationService.buscaPagamentoPorId(pagamentoId);

    assertNotNull(response);
    assertEquals(pagamentoId, response.getIdPagamento());
    assertEquals(pedido.getId(), response.getPedidoId());

    verify(pagamentoRepository).buscaPagamentoPorId(pagamentoId);
  }

  @Test
  void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId))
        .thenThrow(
            new APIException(
                HttpStatus.NOT_FOUND, ErrorCode.PAGAMENTO_NAO_ENCONTRADO, pagamentoId));

    APIException exception =
        assertThrows(
            APIException.class, () -> pagamentoApplicationService.buscaPagamentoPorId(pagamentoId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
    assertEquals(ErrorCode.PAGAMENTO_NAO_ENCONTRADO, exception.getErrorCode());

    verify(pagamentoRepository).buscaPagamentoPorId(pagamentoId);
  }

  @Test
  void deveProcessarPagamentoComProcessadorCorreto() {
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(pedido.getFormaPagamento()))
        .thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);

    pagamentoApplicationService.processaPagamento(pagamentoRequest);

    verify(processadorPagamento).processar(any(Pagamento.class), eq(pedido));
  }

  @Test
  void deveCriarPagamentoComValorCorreto() {
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(pedido.getFormaPagamento()))
        .thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);

    PagamentoResponse response = pagamentoApplicationService.processaPagamento(pagamentoRequest);

    assertEquals(pedido.getValorTotal(), response.getValor());
    verify(pagamentoRepository).salva(any(Pagamento.class));
  }

  @Test
  void deveUsarProcessadorCorretoParaCartaoCredito() {
    pedido.setFormaPagamento(FormaPagamento.CARTAO_CREDITO);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(FormaPagamento.CARTAO_CREDITO))
        .thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);

    pagamentoApplicationService.processaPagamento(pagamentoRequest);

    verify(processadorFactory).obterProcessador(FormaPagamento.CARTAO_CREDITO);
    verify(processadorPagamento).processar(any(Pagamento.class), eq(pedido));
  }

  @Test
  void deveUsarProcessadorCorretoParaPix() {
    pedido.setFormaPagamento(FormaPagamento.PIX);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(FormaPagamento.PIX)).thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);

    pagamentoApplicationService.processaPagamento(pagamentoRequest);

    verify(processadorFactory).obterProcessador(FormaPagamento.PIX);
    verify(processadorPagamento).processar(any(Pagamento.class), eq(pedido));
  }

  @Test
  void deveUsarProcessadorCorretoParaBoleto() {
    pedido.setFormaPagamento(FormaPagamento.BOLETO);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(pedidoId)).thenReturn(pedido);
    when(processadorFactory.obterProcessador(FormaPagamento.BOLETO))
        .thenReturn(processadorPagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);

    pagamentoApplicationService.processaPagamento(pagamentoRequest);

    verify(processadorFactory).obterProcessador(FormaPagamento.BOLETO);
    verify(processadorPagamento).processar(any(Pagamento.class), eq(pedido));
  }

  @Test
  void deveRetornarPagamentoImediatoParaCartaoCredito() {
    ProcessadorPagamento processador =
        realProcessadorFactory.obterProcessador(FormaPagamento.CARTAO_CREDITO);
    assertEquals(pagamentoImediatoStrategy, processador);
  }

  @Test
  void deveRetornarPagamentoImediatoParaCartaoDebito() {
    ProcessadorPagamento processador =
        realProcessadorFactory.obterProcessador(FormaPagamento.CARTAO_DEBITO);
    assertEquals(pagamentoImediatoStrategy, processador);
  }

  @Test
  void deveRetornarPagamentoImediatoParaDinheiro() {
    ProcessadorPagamento processador =
        realProcessadorFactory.obterProcessador(FormaPagamento.DINHEIRO);
    assertEquals(pagamentoImediatoStrategy, processador);
  }

  @Test
  void deveRetornarPagamentoAguardandoParaPix() {
    ProcessadorPagamento processador = realProcessadorFactory.obterProcessador(FormaPagamento.PIX);
    assertEquals(pagamentoAguardandoStrategy, processador);
  }

  @Test
  void deveRetornarPagamentoAguardandoParaBoleto() {
    ProcessadorPagamento processador =
        realProcessadorFactory.obterProcessador(FormaPagamento.BOLETO);
    assertEquals(pagamentoAguardandoStrategy, processador);
  }

  @Test
  void pagamentoImediatoStrategyDeveProcessarCorretamente() {
    PagamentoImediatoStrategy strategy = new PagamentoImediatoStrategy();
    Pagamento pagamentoTeste = PagamentoDataHelper.criaPagamentoValido(pedido);

    strategy.processar(pagamentoTeste, pedido);

    assertEquals(StatusPagamento.PAGO, pagamentoTeste.getStatusPagamento());
    assertEquals(StatusPedido.PAGO, pedido.getStatus());
  }

  @Test
  void pagamentoAguardandoStrategyDeveProcessarCorretamente() {
    PagamentoAguardandoStrategy strategy = new PagamentoAguardandoStrategy();
    Pagamento pagamentoTeste = PagamentoDataHelper.criaPagamentoValido(pedido);

    strategy.processar(pagamentoTeste, pedido);

    assertEquals(StatusPagamento.AGUARDANDO, pagamentoTeste.getStatusPagamento());
    assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
  }

  @Test
  void deveBuscarPagamentosSemFiltroComPaginacao() {
    int page = 0;
    int size = 10;
    Pagamento pagamanto1 = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamanto1.setValor(new BigDecimal("100.00"));
    Pagamento pagamanto2 = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamanto2.setValor(new BigDecimal("200.00"));

    Pageable pageable = PageRequest.of(page, size);
    Page<Pagamento> pagamentos = new PageImpl<>(List.of(pagamanto1, pagamanto2), pageable, 2);

    doReturn(pagamentos)
        .when(pagamentoRepository)
        .buscaPagamentosPaginado(isNull(), any(Pageable.class));

    PagamentoPageResponse pagamentoPageResponse =
        pagamentoApplicationService.buscaPagamentosPaginado(null, page, size);

    assertNotNull(pagamentoPageResponse);
    assertEquals(2, pagamentoPageResponse.getTotalPagamentos());
    assertEquals(1, pagamentoPageResponse.getTotalPaginas());
    assertTrue(
        new BigDecimal("300.0").compareTo(pagamentoPageResponse.getValorTotalPagamentos()) == 0);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(pagamentoRepository).buscaPagamentosPaginado(isNull(), pageableCaptor.capture());
    Pageable pageableEnviado = pageableCaptor.getValue();
    assertEquals(page, pageableEnviado.getPageNumber());
    assertTrue(pageableEnviado.getSort().getOrderFor("dataPagamento").isDescending());
  }

  @Test
  void deveBuscarPagamentosComFiltroEPaginacao() {
    int page = 0;
    int size = 10;
    Pagamento pagamanto1 = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamanto1.setValor(new BigDecimal("100.00"));
    Pagamento pagamanto2 = PagamentoDataHelper.criaPagamentoValido(pedido);
    pagamanto2.setValor(new BigDecimal("200.00"));

    Pageable pageable = PageRequest.of(page, size);
    Page<Pagamento> pagamentos = new PageImpl<>(List.of(pagamanto1, pagamanto2), pageable, 2);

    doReturn(pagamentos)
        .when(pagamentoRepository)
        .buscaPagamentosPaginado(eq(StatusPagamento.PAGO), any(Pageable.class));

    PagamentoPageResponse pagamentoPageResponse =
        pagamentoApplicationService.buscaPagamentosPaginado(
            StatusPagamento.valueOf("PAGO"), page, size);

    assertNotNull(pagamentoPageResponse);
    assertEquals(2, pagamentoPageResponse.getTotalPagamentos());
    assertEquals(1, pagamentoPageResponse.getTotalPaginas());
    assertTrue(
        new BigDecimal("300.0").compareTo(pagamentoPageResponse.getValorTotalPagamentos()) == 0);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(pagamentoRepository)
        .buscaPagamentosPaginado(eq(StatusPagamento.PAGO), pageableCaptor.capture());
    Pageable pageableEnviado = pageableCaptor.getValue();
    assertEquals(page, pageableEnviado.getPageNumber());
    assertTrue(pageableEnviado.getSort().getOrderFor("dataPagamento").isDescending());
  }

  @Test
  void deveBuscarPagamentoPorIdPedidoComSucesso() {
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId))
        .thenReturn(Optional.of(pagamento));

    var response = pagamentoApplicationService.buscaPagamentoPorIdPedido(pedidoId);

    assertNotNull(response);
    assertEquals(pagamento.getStatusPagamento(), response.getStatusPagamento());
    assertEquals(pagamento.getValor(), response.getValor());
    verify(pagamentoRepository).buscaPagamentoPorPedidoId(pedidoId);
  }

  @Test
  void deveLancarExcecaoQuandoPedidoNaoPossuiPagamento() {
    when(pagamentoRepository.buscaPagamentoPorPedidoId(pedidoId)).thenReturn(Optional.empty());

    APIException exception =
        assertThrows(
            APIException.class,
            () -> pagamentoApplicationService.buscaPagamentoPorIdPedido(pedidoId));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
    assertEquals(ErrorCode.PEDIDO_NAO_POSSUI_PAGAMENTO, exception.getErrorCode());
    verify(pagamentoRepository).buscaPagamentoPorPedidoId(pedidoId);
  }

  @Test
  void deveCancelarPagamentoComSucesso() {
    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
    when(pagamentoRepository.salva(any(Pagamento.class))).thenAnswer(i -> i.getArgument(0));
    when(pedidoRepository.salva(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

    pagamentoApplicationService.cancelaPagamento(pagamentoId, cancelaPagamentoRequest);

    ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
    ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);

    verify(pagamentoRepository).salva(pagamentoCaptor.capture());
    verify(pedidoRepository).salva(pedidoCaptor.capture());

    Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
    Pedido pedidoSalvo = pedidoCaptor.getValue();

    assertEquals(StatusPagamento.FALHOU, pagamentoSalvo.getStatusPagamento());
    assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedidoSalvo.getStatus());
    assertEquals("Desisti da compra", pagamentoSalvo.getMotivoCancelamento());
  }

  @Test
  void deveLancarErroQuandoPagamentoJaEstiverPago() {
    pagamento.setStatusPagamento(StatusPagamento.PAGO);
    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);

    APIException ex =
        assertThrows(
            APIException.class,
            () ->
                pagamentoApplicationService.cancelaPagamento(pagamentoId, cancelaPagamentoRequest));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
    assertEquals(ErrorCode.PAGAMENTO_JA_PROCESSADO, ex.getErrorCode());

    verify(pagamentoRepository, never()).salva(any());
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveReprocessarPagamentoComSucesso() {
    pagamento.setStatusPagamento(StatusPagamento.FALHOU);
    pagamento.setTentativasPagamento(1);

    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
    when(processadorFactory.obterProcessador(pedido.getFormaPagamento()))
            .thenReturn(processadorPagamento);

    var response = pagamentoApplicationService.reprocessaPagamento(pagamentoId);

    assertNotNull(response);
    assertEquals(StatusPagamento.AGUARDANDO, pagamento.getStatusPagamento());
    assertEquals(2, pagamento.getTentativasPagamento());

    verify(processadorFactory).obterProcessador(pedido.getFormaPagamento());
    verify(processadorPagamento).processar(pagamento, pedido);
    verify(pagamentoRepository).salva(pagamento);
    verify(pedidoRepository).salva(pedido);
  }

  @Test
  void deveLancarExcecaoQuandoLimiteTentativasExcedido() {
    pagamento.setStatusPagamento(StatusPagamento.FALHOU);
    pagamento.setTentativasPagamento(3);

    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);

    APIException exception = assertThrows(
            APIException.class,
            () -> pagamentoApplicationService.reprocessaPagamento(pagamentoId)
    );

    assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    assertEquals(ErrorCode.LIMITE_DE_TENTATIVAS_EXCEDIDO, exception.getErrorCode());

    verify(pagamentoRepository).buscaPagamentoPorId(pagamentoId);
    verify(processadorFactory, never()).obterProcessador(any());
  }

  @Test
  void deveLancarExcecaoQuandoPagamentoJaPago() {
    pagamento.setStatusPagamento(StatusPagamento.PAGO);

    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);

    APIException exception = assertThrows(
            APIException.class,
            () -> pagamentoApplicationService.reprocessaPagamento(pagamentoId)
    );

    assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    assertEquals(ErrorCode.PAGAMENTO_JA_PROCESSADO_COM_SUCESSO, exception.getErrorCode());

    verify(pagamentoRepository).buscaPagamentoPorId(pagamentoId);
    verify(processadorFactory, never()).obterProcessador(any());
  }

  @Test
  void deveLancarExcecaoQuandoPagamentoNaoForFalhou() {
    pagamento.setStatusPagamento(StatusPagamento.AGUARDANDO);

    when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);

    APIException exception = assertThrows(
            APIException.class,
            () -> pagamentoApplicationService.reprocessaPagamento(pagamentoId)
    );

    assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    assertEquals(ErrorCode.PAGAMENTO_NAO_PODE_SER_REPROCESSADO, exception.getErrorCode());

    verify(pagamentoRepository).buscaPagamentoPorId(pagamentoId);
    verify(processadorFactory, never()).obterProcessador(any());
  }
}
