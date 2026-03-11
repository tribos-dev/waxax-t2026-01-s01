package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

public record PedidoListResponse(
    UUID idPedido, LocalDateTime dataPedido, StatusPedido status, BigDecimal valorTotal) {

  public PedidoListResponse(Pedido pedido) {
    this(pedido.getId(), pedido.getDataPedido(), pedido.getStatus(), pedido.getValorTotal());
  }
}
