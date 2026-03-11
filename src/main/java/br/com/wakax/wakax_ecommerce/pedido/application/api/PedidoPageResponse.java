package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.util.List;
import java.util.UUID;

public record PedidoPageResponse(
    UUID idCliente, List<PedidoListResponse> pedidos, Integer totalPedidos, Integer totalPaginas) {

  public PedidoPageResponse(
      UUID idCliente, List<PedidoListResponse> pedidos, Integer totalPaginas) {
    this(idCliente, pedidos, pedidos.size(), totalPaginas);
  }
}
