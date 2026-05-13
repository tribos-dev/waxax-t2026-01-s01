package br.com.wakax.wakax_ecommerce.pedido.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.ItemCarrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pagamento.application.repository.PagamentoRepository;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoPageResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.CancelamentoPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.EnderecoEntregaRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

@ExtendWith(MockitoExtension.class)
class PedidoApplicationServiceTest {

  @Mock private PedidoRepository pedidoRepository;
  @Mock private CarrinhoRepository carrinhoRepository;
  @Mock private ClienteService clienteService;
  @Mock private EstoqueService estoqueService;
  @Mock private PagamentoRepository pagamentoRepository;

  @InjectMocks private PedidoApplicationService applicationService;

  @Test
  void deveCadastrarPedidoComSucesso() {
    UUID idCarrinho = UUID.randomUUID();
    UUID idCliente = UUID.randomUUID();

    PedidoRequest request = mock(PedidoRequest.class);
    when(request.getIdCarrinho()).thenReturn(idCarrinho);
    when(request.getFormaPagamento()).thenReturn(FormaPagamento.CARTAO_CREDITO);

    Carrinho carrinho = mock(Carrinho.class);
    Cliente cliente = mock(Cliente.class);
    Pessoa pessoa = mock(Pessoa.class);
    Endereco endereco = mock(Endereco.class);
    Produto produto = mock(Produto.class);
    Preco preco = mock(Preco.class);
    ItemCarrinho itemCarrinho = mock(ItemCarrinho.class);

    when(carrinho.getCliente()).thenReturn(cliente);
    when(cliente.getId()).thenReturn(idCliente);
    when(cliente.getPessoa()).thenReturn(pessoa);
    when(pessoa.getNome()).thenReturn("Cliente Teste");
    when(pessoa.getEnderecos()).thenReturn(List.of(endereco));
    when(carrinho.getItensCarrinho()).thenReturn(List.of(itemCarrinho));
    when(itemCarrinho.getProduto()).thenReturn(produto);
    when(itemCarrinho.getQuantidade()).thenReturn(2);
    when(produto.getPrecos()).thenReturn(List.of(preco));
    when(preco.getValor()).thenReturn(new BigDecimal("50.00"));
    when(carrinhoRepository.buscaCarrinhoPorId(idCarrinho)).thenReturn(carrinho);
    when(pedidoRepository.salva(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

    PedidoResponse response = applicationService.cadastraPedido(request);

    assertNotNull(response);
    assertEquals(idCliente, response.getClienteId());
    assertEquals(new BigDecimal("100.00"), response.getValorTotal());
    verify(carrinhoRepository).buscaCarrinhoPorId(idCarrinho);
    verify(pedidoRepository).salva(any(Pedido.class));
    verify(carrinhoRepository).salva(carrinho);
  }

  @Test
  void deveLancarExcecaoAoCadastrarQuandoCarrinhoNaoExiste() {
    UUID idCarrinho = UUID.randomUUID();

    PedidoRequest request = mock(PedidoRequest.class);
    when(request.getIdCarrinho()).thenReturn(idCarrinho);

    when(carrinhoRepository.buscaCarrinhoPorId(idCarrinho))
        .thenThrow(new APIException(null, ErrorCode.CARRINHO_NAO_ENCONTRADO));

    APIException ex =
        assertThrows(APIException.class, () -> applicationService.cadastraPedido(request));
    assertEquals(ErrorCode.CARRINHO_NAO_ENCONTRADO, ex.getErrorCode());

    verify(carrinhoRepository, times(1)).buscaCarrinhoPorId(idCarrinho);
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveBuscarPedidoPorIdComSucesso() {
    UUID idPedido = UUID.randomUUID();
    UUID idCliente = UUID.randomUUID();

    Cliente cliente = mock(Cliente.class);
    Pessoa pessoa = mock(Pessoa.class);
    Endereco endereco = mock(Endereco.class);
    when(cliente.getId()).thenReturn(idCliente);
    when(cliente.getPessoa()).thenReturn(pessoa);
    when(pessoa.getNome()).thenReturn("Cliente Teste");

    Pedido pedido =
        Pedido.builder()
            .id(idPedido)
            .cliente(cliente)
            .dataPedido(LocalDateTime.now())
            .itensPedido(List.of())
            .valorTotal(new BigDecimal("10.00"))
            .formaPagamento(FormaPagamento.CARTAO_DEBITO)
            .enderecoEntrega(endereco)
            .build();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);

    PedidoResponse response = applicationService.buscaPedidoPorId(idPedido);

    assertNotNull(response);
    assertEquals(idPedido, response.getIdPedido());
    assertEquals(idCliente, response.getClienteId());
    assertEquals(new BigDecimal("10.00"), response.getValorTotal());
    verify(pedidoRepository, times(1)).buscaPedidoPorId(idPedido);
    verifyNoMoreInteractions(pedidoRepository);
    verifyNoInteractions(carrinhoRepository);
  }

  @Test
  void deveLancarExcecaoQuandoPedidoNaoExiste() {
    UUID idPedido = UUID.randomUUID();

    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenThrow(new APIException(null, ErrorCode.PEDIDO_NAO_ENCONTRADO));

    APIException ex =
        assertThrows(APIException.class, () -> applicationService.buscaPedidoPorId(idPedido));
    assertEquals(ErrorCode.PEDIDO_NAO_ENCONTRADO, ex.getErrorCode());

    verify(pedidoRepository, times(1)).buscaPedidoPorId(idPedido);
    verifyNoMoreInteractions(pedidoRepository);
    verifyNoInteractions(carrinhoRepository);
  }

  @Test
  void cadastraPedido_deveRetornarResponseComIdEValoresCorretos() {
    UUID idCarrinho = UUID.randomUUID();
    UUID idPedido = UUID.randomUUID();
    UUID idCliente = UUID.randomUUID();

    PedidoRequest request = mock(PedidoRequest.class);
    when(request.getIdCarrinho()).thenReturn(idCarrinho);
    when(request.getFormaPagamento()).thenReturn(FormaPagamento.CARTAO_CREDITO);

    Carrinho carrinho = mock(Carrinho.class);
    Cliente cliente = mock(Cliente.class);
    Pessoa pessoa = mock(Pessoa.class);
    Endereco endereco = mock(Endereco.class);
    ItemCarrinho itemCarrinho = mock(ItemCarrinho.class);
    Produto produto = mock(Produto.class);
    Preco preco = mock(Preco.class);

    when(carrinho.getCliente()).thenReturn(cliente);
    when(cliente.getId()).thenReturn(idCliente);
    when(cliente.getPessoa()).thenReturn(pessoa);
    when(pessoa.getNome()).thenReturn("Cliente Teste");
    when(pessoa.getEnderecos()).thenReturn(List.of(endereco));
    when(carrinho.getItensCarrinho()).thenReturn(List.of(itemCarrinho));
    when(itemCarrinho.getProduto()).thenReturn(produto);
    when(itemCarrinho.getQuantidade()).thenReturn(2);
    when(produto.getPrecos()).thenReturn(List.of(preco));
    when(preco.getValor()).thenReturn(new BigDecimal("50.00"));
    when(carrinhoRepository.buscaCarrinhoPorId(idCarrinho)).thenReturn(carrinho);
    when(pedidoRepository.salva(any(Pedido.class)))
        .thenAnswer(
            inv -> {
              Pedido p = inv.getArgument(0);
              p.setId(idPedido);
              return p;
            });

    PedidoResponse resp = applicationService.cadastraPedido(request);

    assertNotNull(resp);
    assertEquals(idPedido, resp.getIdPedido());
    assertEquals(idCliente, resp.getClienteId());
    assertEquals(new BigDecimal("100.00"), resp.getValorTotal());
    assertEquals(FormaPagamento.CARTAO_CREDITO, resp.getFormaPagamento());
    assertNotNull(resp.getEnderecoEntrega());
    verify(carrinhoRepository, times(1)).buscaCarrinhoPorId(idCarrinho);
    verify(pedidoRepository, times(1)).salva(any(Pedido.class));
    verify(carrinhoRepository, times(1)).salva(carrinho);
  }

  @Test
  void buscaPedidoPorId_deveRetornarResponseMapeada() {
    UUID idPedido = UUID.randomUUID();
    UUID idCliente = UUID.randomUUID();

    Produto produto = mock(Produto.class);
    when(produto.getId()).thenReturn(UUID.randomUUID());
    when(produto.getDescricao()).thenReturn("Produto X");

    Endereco endereco = new Endereco();
    Pessoa pessoa = new Pessoa();
    pessoa.setNome("Fulano");
    pessoa.setEnderecos(List.of(endereco));

    Cliente cliente =
        Cliente.builder()
            .id(idCliente)
            .pessoa(pessoa)
            .dataCriacao(LocalDateTime.now())
            .dataEdicao(LocalDateTime.now())
            .build();

    ItemPedido item =
        ItemPedido.builder()
            .produto(produto)
            .quantidade(3)
            .valorUnitario(new BigDecimal("10.00"))
            .build();

    Pedido pedido =
        Pedido.builder()
            .id(idPedido)
            .cliente(cliente)
            .dataPedido(LocalDateTime.now())
            .status(StatusPedido.CRIADO)
            .itensPedido(List.of(item))
            .valorTotal(new BigDecimal("30.00"))
            .formaPagamento(FormaPagamento.PIX)
            .enderecoEntrega(endereco)
            .build();
    item.setPedido(pedido);

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);

    PedidoResponse resp = applicationService.buscaPedidoPorId(idPedido);

    assertNotNull(resp);
    assertEquals(idPedido, resp.getIdPedido());
    assertEquals(idCliente, resp.getClienteId());
    assertEquals("Fulano", resp.getNomeCliente());
    assertEquals(StatusPedido.CRIADO, resp.getStatus());
    assertEquals(new BigDecimal("30.00"), resp.getValorTotal());
    assertEquals(FormaPagamento.PIX, resp.getFormaPagamento());
    assertEquals(1, resp.getItensPedido().size());
    assertEquals(3, resp.getItensPedido().get(0).getQuantidade());
    verify(pedidoRepository, times(1)).buscaPedidoPorId(idPedido);
    verifyNoMoreInteractions(pedidoRepository);
    verifyNoInteractions(carrinhoRepository);
  }

  @Test
  void buscaPedidoPorId_devePropagarAPIExceptionQuandoNaoEncontrado() {
    UUID idPedido = UUID.randomUUID();
    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenThrow(
            new APIException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                ErrorCode.PEDIDO_NAO_ENCONTRADO,
                idPedido));

    APIException ex =
        assertThrows(APIException.class, () -> applicationService.buscaPedidoPorId(idPedido));
    assertEquals(ErrorCode.PEDIDO_NAO_ENCONTRADO, ex.getErrorCode());
    verify(pedidoRepository, times(1)).buscaPedidoPorId(idPedido);
    verifyNoMoreInteractions(pedidoRepository);
    verifyNoInteractions(carrinhoRepository);
  }

  @Test
  void deveLancarExcecaoQuandoClienteEstiverInativo() {
    UUID idCarrinho = UUID.randomUUID();
    PedidoRequest request = mock(PedidoRequest.class);
    when(request.getIdCarrinho()).thenReturn(idCarrinho);

    Carrinho carrinho = mock(Carrinho.class);
    Cliente cliente = mock(Cliente.class);
    when(carrinho.getCliente()).thenReturn(cliente);
    when(carrinhoRepository.buscaCarrinhoPorId(idCarrinho)).thenReturn(carrinho);
    doThrow(new APIException(HttpStatus.FORBIDDEN, ErrorCode.CLIENTE_INATIVO))
        .when(cliente)
        .validaSeClienteEstaAtivo();

    APIException ex =
        assertThrows(APIException.class, () -> applicationService.cadastraPedido(request));

    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusException());
    verify(pedidoRepository, never()).salva(any());
  }

  void deveListarPedidosDoClienteOrdenadosDoMaisRecenteParaOMaisAntigo() {
    UUID idCliente = UUID.randomUUID();
    int page = 0;
    int size = 10;
    LocalDateTime agora = LocalDateTime.now();

    Pedido pedidoMaisRecente =
        PedidoDataHelper.criaPedidoResumo(agora, StatusPedido.PAGO, new BigDecimal("199.90"));
    Pedido pedidoMaisAntigo =
        PedidoDataHelper.criaPedidoResumo(
            agora.minusDays(2), StatusPedido.CRIADO, new BigDecimal("79.90"));

    Page<Pedido> paginaPedidos =
        new PageImpl<>(List.of(pedidoMaisRecente, pedidoMaisAntigo), PageRequest.of(page, size), 2);

    when(pedidoRepository.buscaPedidosDoClientePaginado(
            eq(idCliente), isNull(), any(Pageable.class)))
        .thenReturn(paginaPedidos);

    PedidoPageResponse response =
        applicationService.buscaPedidosDoCliente(idCliente, null, page, size);

    assertNotNull(response);
    assertEquals(idCliente, response.idCliente());
    assertEquals(2, response.totalPedidos());
    assertEquals(1, response.totalPaginas());

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(clienteService).buscaClienteEspecifico(idCliente);
    verify(pedidoRepository)
        .buscaPedidosDoClientePaginado(eq(idCliente), isNull(), pageableCaptor.capture());
    Pageable pageableEnviado = pageableCaptor.getValue();
    assertEquals(page, pageableEnviado.getPageNumber());
    assertEquals(size, pageableEnviado.getPageSize());
  }

  @Test
  void deveRetornarListaVaziaQuandoClienteNaoPossuiPedidos() {
    UUID idCliente = UUID.randomUUID();
    int page = 0;
    int size = 10;
    Page<Pedido> paginaVazia = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

    when(pedidoRepository.buscaPedidosDoClientePaginado(
            eq(idCliente), isNull(), any(Pageable.class)))
        .thenReturn(paginaVazia);

    PedidoPageResponse response =
        applicationService.buscaPedidosDoCliente(idCliente, null, page, size);

    assertNotNull(response);
    assertEquals(idCliente, response.idCliente());
    assertTrue(response.pedidos().isEmpty());
    assertEquals(0, response.totalPedidos());
    assertEquals(0, response.totalPaginas());
    verify(clienteService).buscaClienteEspecifico(idCliente);
    verify(pedidoRepository)
        .buscaPedidosDoClientePaginado(eq(idCliente), isNull(), any(Pageable.class));
  }

  @Test
  void deveListarPedidosComDiferentesStatus() {
    UUID idCliente = UUID.randomUUID();
    int page = 0;
    int size = 10;
    LocalDateTime agora = LocalDateTime.now();

    Page<Pedido> paginaPedidos =
        new PageImpl<>(
            List.of(
                PedidoDataHelper.criaPedidoResumo(
                    agora, StatusPedido.CRIADO, new BigDecimal("10.00")),
                PedidoDataHelper.criaPedidoResumo(
                    agora.minusHours(1), StatusPedido.PAGO, new BigDecimal("20.00")),
                PedidoDataHelper.criaPedidoResumo(
                    agora.minusHours(2), StatusPedido.ENVIADO, new BigDecimal("30.00")),
                PedidoDataHelper.criaPedidoResumo(
                    agora.minusHours(3), StatusPedido.ENTREGUE, new BigDecimal("40.00")),
                PedidoDataHelper.criaPedidoResumo(
                    agora.minusHours(4), StatusPedido.CANCELADO, new BigDecimal("50.00"))),
            PageRequest.of(page, size),
            5);

    when(pedidoRepository.buscaPedidosDoClientePaginado(
            eq(idCliente), isNull(), any(Pageable.class)))
        .thenReturn(paginaPedidos);

    PedidoPageResponse response =
        applicationService.buscaPedidosDoCliente(idCliente, null, page, size);

    assertNotNull(response);
    assertEquals(5, response.totalPedidos());
    Set<StatusPedido> statusRetornados =
        response.pedidos().stream().map(pedido -> pedido.status()).collect(Collectors.toSet());
    assertTrue(statusRetornados.contains(StatusPedido.CRIADO));
    assertTrue(statusRetornados.contains(StatusPedido.PAGO));
    assertTrue(statusRetornados.contains(StatusPedido.ENVIADO));
    assertTrue(statusRetornados.contains(StatusPedido.ENTREGUE));
    assertTrue(statusRetornados.contains(StatusPedido.CANCELADO));
  }

  @Test
  void deveAplicarFiltroOpcionalPorStatusNaBuscaPedidosDoCliente() {
    UUID idCliente = UUID.randomUUID();
    int page = 0;
    int size = 10;
    StatusPedido statusFiltro = StatusPedido.PAGO;
    Pedido pedidoPago =
        PedidoDataHelper.criaPedidoResumo(
            LocalDateTime.now(), StatusPedido.PAGO, new BigDecimal("99.90"));
    Page<Pedido> paginaPedidos = new PageImpl<>(List.of(pedidoPago), PageRequest.of(page, size), 1);

    when(pedidoRepository.buscaPedidosDoClientePaginado(
            eq(idCliente), eq(statusFiltro), any(Pageable.class)))
        .thenReturn(paginaPedidos);

    PedidoPageResponse response =
        applicationService.buscaPedidosDoCliente(idCliente, statusFiltro, page, size);

    assertNotNull(response);
    assertEquals(1, response.totalPedidos());
    assertEquals(StatusPedido.PAGO, response.pedidos().get(0).status());
    verify(clienteService).buscaClienteEspecifico(idCliente);
    verify(pedidoRepository)
        .buscaPedidosDoClientePaginado(eq(idCliente), eq(statusFiltro), any(Pageable.class));
  }

  @Test
  void deveCancelarPedidoElegivelComSucesso() {
    Pedido pedido = PedidoDataHelper.criarPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
    UUID idPedido = pedido.getId();
    CancelamentoPedidoRequest request = new CancelamentoPedidoRequest("Cliente desistiu da compra");

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(idPedido)).thenReturn(Optional.empty());

    applicationService.cancelarPedido(idPedido, request);

    assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    assertEquals(request.getMotivoCancelamento(), pedido.getMotivoCancelamento());
    verify(estoqueService).liberaReservaDePedido(pedido.getItensPedido());
    verify(pedidoRepository).salva(pedido);
    verify(pagamentoRepository, never()).salva(org.mockito.ArgumentMatchers.any(Pagamento.class));
  }

  @Test
  void deveEstornarPagamentoQuandoPedidoCanceladoEstiverPago() {
    Pedido pedido = PedidoDataHelper.criarPedido(StatusPedido.PAGO);
    UUID idPedido = pedido.getId();
    CancelamentoPedidoRequest request = new CancelamentoPedidoRequest("Falha na entrega");
    Pagamento pagamento =
        Pagamento.builder()
            .id(UUID.randomUUID())
            .pedido(pedido)
            .statusPagamento(StatusPagamento.PAGO)
            .dataPagamento(LocalDateTime.now())
            .valor(BigDecimal.TEN)
            .build();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(pagamentoRepository.buscaPagamentoPorPedidoId(idPedido))
        .thenReturn(Optional.of(pagamento));

    applicationService.cancelarPedido(idPedido, request);

    assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
    assertEquals(StatusPagamento.ESTORNADO, pagamento.getStatusPagamento());
    assertEquals(request.getMotivoCancelamento(), pagamento.getMotivoEstorno());
    verify(pagamentoRepository).salva(pagamento);
    verify(estoqueService).liberaReservaDePedido(pedido.getItensPedido());
    verify(pedidoRepository).salva(pedido);
  }

  @Test
  void deveFalharAoCancelarPedidoNaoElegivelESemAlteracoes() {
    UUID idPedido = UUID.randomUUID();
    Pedido pedido = PedidoDataHelper.criarPedido(StatusPedido.ENVIADO);
    CancelamentoPedidoRequest request = new CancelamentoPedidoRequest("Mudanca de plano");

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.cancelarPedido(idPedido, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.TRANSICAO_STATUS_INVALIDA, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(pedido);
    verifyNoInteractions(estoqueService);
    verify(pagamentoRepository, never()).salva(any(Pagamento.class));
  }

  @Test
  void deveFalharAoCancelarPedidoInexistente() {
    UUID idPedido = UUID.randomUUID();
    CancelamentoPedidoRequest request = new CancelamentoPedidoRequest("Pedido nao encontrado");

    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenThrow(
            new APIException(HttpStatus.NOT_FOUND, ErrorCode.PEDIDO_NAO_ENCONTRADO, idPedido));

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.cancelarPedido(idPedido, request));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    assertEquals(ErrorCode.PEDIDO_NAO_ENCONTRADO, ex.getErrorCode());
    verifyNoInteractions(estoqueService);
    verifyNoInteractions(pagamentoRepository);
  }

  @Test
  void deveAlterarEnderecoComSucesso() {
    UUID idEnderecoNovo = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoNovo);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(UUID.randomUUID());
    Endereco enderecoNovo = PedidoDataHelper.criaEndereco(idEnderecoNovo);

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.CRIADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoNovo)).thenReturn(enderecoNovo);

    applicationService.alteraEnderecoEntrega(idPedido, request);

    assertEquals(enderecoNovo, pedido.getEnderecoEntrega());
    verify(pedidoRepository, times(1)).salva(pedido);
  }

  @Test
  void deveLancarExcecaoQuandoEnderecoForIncompleto() {
    UUID idEnderecoNovo = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoNovo);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(UUID.randomUUID());
    Endereco enderecoNovo = PedidoDataHelper.criaEnderecoIncompleto(idEnderecoNovo);

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.CRIADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoNovo)).thenReturn(enderecoNovo);

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.alteraEnderecoEntrega(idPedido, request));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.ENDERECO_INCOMPLETO, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoNovoEnderecoForIgualAtual() {
    UUID idEnderecoAtual = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoAtual);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(idEnderecoAtual);

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.CRIADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoAtual)).thenReturn(enderecoAtual);

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.alteraEnderecoEntrega(idPedido, request));
    assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
    assertEquals(ErrorCode.PEDIDO_MESMO_ENDERECO, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoPedidoEstiverCancelado() {
    UUID idEnderecoNovo = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoNovo);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(UUID.randomUUID());
    Endereco enderecoNovo = PedidoDataHelper.criaEndereco(idEnderecoNovo);

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.CANCELADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoNovo)).thenReturn(enderecoNovo);

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.alteraEnderecoEntrega(idPedido, request));
    assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
    assertEquals(ErrorCode.PEDIDO_JA_CANCELADO, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoPedidoEstiverEnviadoOuEntregue() {
    UUID idEnderecoNovo = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoNovo);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(UUID.randomUUID());
    Endereco enderecoNovo = PedidoDataHelper.criaEndereco(idEnderecoNovo);

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.ENVIADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoNovo)).thenReturn(enderecoNovo);

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.alteraEnderecoEntrega(idPedido, request));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.PEDIDO_JA_ENVIADO, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoEnderecoNaoEncontradoNaListaDoCliente() {
    UUID idEnderecoNovo = UUID.randomUUID();
    EnderecoEntregaRequest request = PedidoDataHelper.criaEnderecoEntregaRequest(idEnderecoNovo);

    Cliente cliente = mock(Cliente.class);
    Endereco enderecoAtual = PedidoDataHelper.criaEndereco(UUID.randomUUID());

    Pedido pedido = PedidoDataHelper.criaPedido(StatusPedido.CRIADO, cliente, enderecoAtual);
    UUID idPedido = pedido.getId();

    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(cliente.buscaEnderecoEspecifico(idEnderecoNovo))
        .thenThrow(new APIException(HttpStatus.NOT_FOUND, ErrorCode.ENDERECO_NAO_ENCONTRADO));

    APIException ex =
        assertThrows(
            APIException.class, () -> applicationService.alteraEnderecoEntrega(idPedido, request));
    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    assertEquals(ErrorCode.ENDERECO_NAO_ENCONTRADO, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
  }
}
