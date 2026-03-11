package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

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
}
