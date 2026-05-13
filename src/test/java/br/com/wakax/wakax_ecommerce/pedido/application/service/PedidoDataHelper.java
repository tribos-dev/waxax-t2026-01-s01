package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
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
}
