package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.EnderecoEntregaRequest;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

public final class PedidoDataHelper {

  private PedidoDataHelper() {}

  public static Pedido criaPedidoResumo(
      LocalDateTime dataPedido, StatusPedido statusPedido, BigDecimal valorTotal) {
    return Pedido.builder()
        .id(UUID.randomUUID())
        .dataPedido(dataPedido)
        .status(statusPedido)
        .valorTotal(valorTotal)
        .build();
  }

  public static Pedido criarPedido(StatusPedido statusPedido) {
    Cliente cliente = Cliente.builder().id(UUID.randomUUID()).build();
    Endereco enderecoEntrega = Endereco.builder().id(UUID.randomUUID()).build();
    Produto produto = Produto.builder().id(UUID.randomUUID()).build();
    ItemPedido itemPedido =
        ItemPedido.builder().produto(produto).quantidade(1).valorUnitario(BigDecimal.TEN).build();

    Pedido pedido =
        Pedido.builder()
            .id(UUID.randomUUID())
            .cliente(cliente)
            .dataPedido(LocalDateTime.now())
            .status(statusPedido)
            .itensPedido(List.of(itemPedido))
            .valorTotal(BigDecimal.TEN)
            .formaPagamento(FormaPagamento.PIX)
            .enderecoEntrega(enderecoEntrega)
            .dataUltimaAtualizacao(LocalDateTime.now())
            .build();
    itemPedido.setPedido(pedido);
    return pedido;
  }

  public static Endereco criaEndereco(UUID idEndereco) {
    return Endereco.builder()
        .id(idEndereco)
        .logradouro("Rua Teste")
        .numero("123")
        .bairro("Centro")
        .cidade("São Paulo")
        .estado("SP")
        .cep("01000-000")
        .principal(true)
        .build();
  }

  public static Endereco criaEnderecoIncompleto(UUID idEndereco) {
    return Endereco.builder()
        .id(idEndereco)
        .cidade("São Paulo")
        .estado("SP")
        .principal(true)
        .build();
  }

  public static EnderecoEntregaRequest criaEnderecoEntregaRequest(UUID idEndereco) {
    return EnderecoEntregaRequest.builder().idEnderecoEntrega(idEndereco).build();
  }

  public static Pedido criaPedido(StatusPedido status, Cliente cliente, Endereco enderecoAtual) {
    return Pedido.builder()
        .id(UUID.randomUUID())
        .status(status)
        .cliente(cliente)
        .enderecoEntrega(enderecoAtual)
        .build();
  }
}
