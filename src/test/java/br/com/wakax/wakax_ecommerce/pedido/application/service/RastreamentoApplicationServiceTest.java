package br.com.wakax.wakax_ecommerce.pedido.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.wakax.wakax_ecommerce.pedido.domain.HistoricoRastreamento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.HistoricoRastreamentoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.RastreamentoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.HistoricoRastreamentoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.RastreamentoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Rastreamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusRastreamento;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;

@ExtendWith(MockitoExtension.class)
class RastreamentoApplicationServiceTest {

  @Mock private PedidoRepository pedidoRepository;
  @Mock private RastreamentoRepository rastreamentoRepository;

  @InjectMocks private RastreamentoApplicationService rastreamentoApplicationService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void deveCadastrarRastreamentoComEventos() {
    UUID idPedido = UUID.randomUUID();

    when(rastreamentoRepository.buscaRastreamentoPorPedidoIdOptional(idPedido))
        .thenReturn(java.util.Optional.empty());
    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenReturn(Pedido.builder().id(idPedido).build());

    RastreamentoRequest request =
        new RastreamentoRequest(
            "BR123",
            "Correios",
            StatusRastreamento.EM_TRANSITO,
            LocalDate.now().plusDays(3),
            List.of(
                new HistoricoRastreamentoRequest(
                    LocalDateTime.now(), "SP", "Objeto postado", StatusRastreamento.CRIADO)));

    RastreamentoResponse response =
        rastreamentoApplicationService.cadastraRastreamento(idPedido, request);

    assertNotNull(response);
    assertEquals("BR123", response.getCodigo());
    assertEquals("Correios", response.getTransportadora());
    assertEquals(StatusRastreamento.EM_TRANSITO, response.getStatusAtual());
    assertEquals(1, response.getHistorico().size());

    verify(rastreamentoRepository, times(1)).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoRastreamentoJaExiste() {
    UUID idPedido = UUID.randomUUID();
    when(rastreamentoRepository.buscaRastreamentoPorPedidoIdOptional(idPedido))
        .thenReturn(java.util.Optional.of(mock(Rastreamento.class)));

    RastreamentoRequest request =
        new RastreamentoRequest("BR123", "Correios", StatusRastreamento.CRIADO, null, List.of());

    APIException ex =
        assertThrows(
            APIException.class,
            () -> rastreamentoApplicationService.cadastraRastreamento(idPedido, request));

    assertEquals(ErrorCode.RASTREAMENTO_JA_EXISTE, ex.getErrorCode());
    verifyNoInteractions(pedidoRepository);
  }

  @Test
  void deveLancarExcecaoQuandoNaoForDePropriedadeDoCliente() {
    String clientePorEmail = "cliente1@gmail.com";
    String emailDoSolicitante = "cliente2@gmail.com";
    UUID idPedido = UUID.randomUUID();

    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenReturn(
            Pedido.builder()
                .id(idPedido)
                .cliente(
                    Cliente.builder()
                        .pessoa(
                            Pessoa.builder()
                                .emails(List.of(emailDoSolicitante))
                                .build())
                        .build())
                .build());

    APIException ex =
        assertThrows(
            APIException.class,
            () -> rastreamentoApplicationService.consultaRastreamento(clientePorEmail, idPedido));

    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusException());
    assertEquals(ErrorCode.CLIENTE_NAO_E_DONO_DO_PEDIDO, ex.getErrorCode());
    verifyNoInteractions(rastreamentoRepository);
  }

  @Test
  void deveLancarExcecaoQuandoPedidoNaoPossuirRastreamento() {
    UUID idPedido = UUID.randomUUID();
    String clientePorEmail = "cliente1@gmail.com";

    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenReturn(
            Pedido.builder()
                .id(idPedido)
                .cliente(
                    Cliente.builder()
                        .pessoa(
                            Pessoa.builder()
                                .emails(List.of(clientePorEmail)) // O e-mail real do dono
                                .build())
                        .build())
                .build());

    when(rastreamentoRepository.buscaRastreamentoPorPedidoIdOptional(idPedido)).thenReturn(Optional.empty());

    APIException ex =
        assertThrows(
            APIException.class,
            () -> rastreamentoApplicationService.consultaRastreamento(clientePorEmail, idPedido));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    assertEquals(ErrorCode.PEDIDO_NAO_POSSUI_RASTREIO, ex.getErrorCode());

    verify(rastreamentoRepository, times(1)).buscaRastreamentoPorPedidoIdOptional(idPedido);
  }

  @Test
  void deveConsultarRastreamentoDoPedidoComSucesso() {
    UUID idPedido = UUID.randomUUID();
    String clientePorEmail = "cliente1@gmail.com";

    when(pedidoRepository.buscaPedidoPorId(idPedido))
        .thenReturn(
            Pedido.builder()
                .id(idPedido)
                .cliente(
                    Cliente.builder()
                        .pessoa(
                            Pessoa.builder()
                                .emails(List.of(clientePorEmail))
                                .build())
                        .build())
                .build());

    HistoricoRastreamento h1 =
        HistoricoRastreamento.builder()
            .dataEvento(LocalDateTime.now().minusDays(3))
            .local("São Paulo, SP")
            .descricao("Objeto postado no CD Cajamar")
            .status(StatusRastreamento.CRIADO)
            .build();

    HistoricoRastreamento h2 =
        HistoricoRastreamento.builder()
            .dataEvento(LocalDateTime.now().minusDays(1))
            .local("Teresina, PI")
            .descricao("Chegou na unidade de tratamento regional")
            .status(StatusRastreamento.EM_TRANSITO)
            .build();

    Rastreamento rastreamento =
        Rastreamento.builder()
            .codigo("WAX123456")
            .transportadora("MERCADO_LIVRE")
            .statusAtual(StatusRastreamento.EM_TRANSITO)
            .previsaoEntrega(LocalDate.now().plusDays(2))
            .historico(List.of(h2, h1))
            .build();

    when(rastreamentoRepository.buscaRastreamentoPorPedidoIdOptional(idPedido))
        .thenReturn(Optional.ofNullable(rastreamento));

    RastreamentoResponse resultado =
        rastreamentoApplicationService.consultaRastreamento(clientePorEmail, idPedido);

    assertNotNull(resultado);
    assertEquals("WAX123456", resultado.getCodigo());
    assertEquals("MERCADO_LIVRE", resultado.getTransportadora());
    assertEquals(2, resultado.getHistorico().size());

    assertEquals("Teresina, PI", resultado.getHistorico().get(0).getLocal());

    verify(pedidoRepository).buscaPedidoPorId(idPedido);
    verify(rastreamentoRepository).buscaRastreamentoPorPedidoIdOptional(idPedido);
  }
}
