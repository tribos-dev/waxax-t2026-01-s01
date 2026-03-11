package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
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
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoApplicationServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProcessadorPagamentoFactory processadorFactory;
    @Mock
    private ProcessadorPagamento processadorPagamento;
    @Mock
    private PagamentoImediatoStrategy pagamentoImediatoStrategy;
    @Mock
    private PagamentoAguardandoStrategy pagamentoAguardandoStrategy;
    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private PagamentoApplicationService pagamentoApplicationService;
    @InjectMocks
    private ProcessadorPagamentoFactory realProcessadorFactory;

    private PagamentoRequest pagamentoRequest;
    private Pedido pedido;
    private Pagamento pagamento;
    private ItemPedido itemPedido;
    private Estoque estoque;
    private UUID pedidoId;
    private UUID pagamentoId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();
        pagamentoId = UUID.randomUUID();

        pagamentoRequest = PagamentoDataHelper.criaPagamentoRequestValido(pedidoId);
        pedido = PagamentoDataHelper.criaPedidoValido();
        itemPedido = PagamentoDataHelper.criaItemPedidoValido(pedido);
        pedido.setItensPedido(List.of(itemPedido));
        pagamento = PagamentoDataHelper.criaPagamentoValido(pedido);
        pagamento.setId(pagamentoId);
        estoque = PagamentoDataHelper.criaEstoqueValido(itemPedido.getProduto().getId());
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

        assertEquals(StatusPagamento.AGUARDANDO, pagamentoTeste.getStatusPagamento());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
    }

    @Test
    void deveConfirmarPagamentoComSucesso() {
        pagamento.aguardarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        when(estoqueRepository.buscaEstoquePorIdProduto(itemPedido.getProduto().getId())).thenReturn(Optional.of(estoque));
        when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);
        PagamentoResponse response = pagamentoApplicationService.confirmarPagamento(pagamentoId);
        assertNotNull(response);
        assertEquals(StatusPagamento.PAGO, response.getStatusPagamento());
    }

    @Test
    void deveRegistrarDataDeConfirmacaoAoConfirmarPagamento() {
        pagamento.aguardarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        when(estoqueRepository.buscaEstoquePorIdProduto(itemPedido.getProduto().getId())).thenReturn(Optional.of(estoque));
        when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);
        pagamentoApplicationService.confirmarPagamento(pagamentoId);
        assertNotNull(pagamento.getDataConfirmacao());
    }

    @Test
    void deveLiberarPedidoAoConfirmarPagamento() {
        pagamento.aguardarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        when(estoqueRepository.buscaEstoquePorIdProduto(itemPedido.getProduto().getId())).thenReturn(Optional.of(estoque));
        when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);
        pagamentoApplicationService.confirmarPagamento(pagamentoId);
        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        verify(pedidoRepository).salva(pedido);
    }

    @Test
    void deveReservarEstoqueDeItensDoPedidoAoConfirmarPagamento() {
        pagamento.aguardarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        when(estoqueRepository.buscaEstoquePorIdProduto(itemPedido.getProduto().getId())).thenReturn(Optional.of(estoque));
        when(pagamentoRepository.salva(any(Pagamento.class))).thenReturn(pagamento);
        pagamentoApplicationService.confirmarPagamento(pagamentoId);
        verify(estoqueRepository).buscaEstoquePorIdProduto(itemPedido.getProduto().getId());
        verify(estoqueRepository).salva(estoque);

    }

    @Test
    void deveLancarExcecaoQuandoPagamentoJaConfirmado() {
        pagamento.confirmarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        APIException exception = assertThrows(APIException.class, () -> pagamentoApplicationService.confirmarPagamento(pagamentoId));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
        assertEquals(ErrorCode.PAGAMENTO_JA_CONFIRMADO, exception.getErrorCode());
    }

    @Test
    void naoDeveInteragirComEstoqueQuandoPagamentoJaConfirmado() {
        pagamento.confirmarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        assertThrows(APIException.class, () -> pagamentoApplicationService.confirmarPagamento(pagamentoId));
        verify(estoqueRepository, never()).buscaEstoquePorIdProduto(any());
        verify(estoqueRepository, never()).salva(any());
    }

    @Test
    void naoDeveSalvarPedidoNemPagamentoQuandoPagamentoJaConfirmado() {
        pagamento.confirmarPagamento();
        when(pagamentoRepository.buscaPagamentoPorId(pagamentoId)).thenReturn(pagamento);
        assertThrows(APIException.class, () -> pagamentoApplicationService.confirmarPagamento(pagamentoId));
        verify(pedidoRepository, never()).salva(any());
        verify(pagamentoRepository, never()).salva(any());
    }
}


