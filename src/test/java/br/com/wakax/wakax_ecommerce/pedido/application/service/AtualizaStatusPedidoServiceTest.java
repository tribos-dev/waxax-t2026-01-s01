package br.com.wakax.wakax_ecommerce.pedido.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.StatusPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.PedidoStatusEvent;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

@ExtendWith(MockitoExtension.class)
class AtualizaStatusPedidoServiceTest {

  @Mock private PedidoRepository pedidoRepository;
  @Mock private EstoqueRepository estoqueRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository carrinhoRepository;

  @InjectMocks private PedidoApplicationService service;

  private UUID idPedido;
  private Pedido pedido;

  @BeforeEach
  void setUp() {
    idPedido = UUID.randomUUID();
    pedido = criarPedido(StatusPedido.PAGO);
  }

  @Test
  void deveAtualizarStatusDePagoParaEnviado() {
    StatusPedidoRequest request = new StatusPedidoRequest(StatusPedido.ENVIADO);
    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);
    when(pedidoRepository.salva(any(Pedido.class))).thenReturn(pedido);

    service.atualizaStatusPedido(idPedido, request);

    assertEquals(StatusPedido.ENVIADO, pedido.getStatus());
    verify(pedidoRepository).buscaPedidoPorId(idPedido);
    verify(pedidoRepository).salva(pedido);
    verify(eventPublisher).publishEvent(any(PedidoStatusEvent.class));
  }

  @Test
  void deveAtualizarStatusParaCanceladoELiberarEstoque() {
    Pedido pedidoCancelavel = criarPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
    Produto produto = mock(Produto.class);
    when(produto.getId()).thenReturn(UUID.randomUUID());
    Estoque estoque = mock(Estoque.class);

    ItemPedido item =
        ItemPedido.builder().produto(produto).quantidade(5).valorUnitario(BigDecimal.TEN).build();
    pedidoCancelavel.setItensPedido(List.of(item));

    StatusPedidoRequest request = new StatusPedidoRequest(StatusPedido.CANCELADO);
    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedidoCancelavel);
    when(estoqueRepository.buscaEstoquePorIdProduto(any())).thenReturn(Optional.of(estoque));
    when(pedidoRepository.salva(any(Pedido.class))).thenReturn(pedidoCancelavel);

    service.atualizaStatusPedido(idPedido, request);

    assertEquals(StatusPedido.CANCELADO, pedidoCancelavel.getStatus());
    verify(estoque).liberaReserva(5);
    verify(estoqueRepository).salva(estoque);
    verify(eventPublisher).publishEvent(any(PedidoStatusEvent.class));
  }

  @Test
  void deveLancarExcecaoAoTentarTransicaoInvalida() {
    pedido.setStatus(StatusPedido.ENTREGUE);
    StatusPedidoRequest request = new StatusPedidoRequest(StatusPedido.CANCELADO);
    when(pedidoRepository.buscaPedidoPorId(idPedido)).thenReturn(pedido);

    APIException ex =
        assertThrows(APIException.class, () -> service.atualizaStatusPedido(idPedido, request));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusException());
    assertEquals(ErrorCode.TRANSICAO_STATUS_INVALIDA, ex.getErrorCode());
    verify(pedidoRepository, never()).salva(any());
    verify(eventPublisher, never()).publishEvent(any());
  }


  private Pedido criarPedido(StatusPedido status) {
    Cliente cliente = mock(Cliente.class);
    Pessoa pessoa = mock(Pessoa.class);
    Endereco endereco = mock(Endereco.class);
    lenient().when(cliente.getPessoa()).thenReturn(pessoa);

    return Pedido.builder()
        .id(idPedido)
        .cliente(cliente)
        .dataPedido(LocalDateTime.now())
        .dataUltimaAtualizacao(LocalDateTime.now())
        .status(status)
        .itensPedido(List.of())
        .valorTotal(BigDecimal.TEN)
        .formaPagamento(FormaPagamento.PIX)
        .enderecoEntrega(endereco)
        .build();
  }
}
