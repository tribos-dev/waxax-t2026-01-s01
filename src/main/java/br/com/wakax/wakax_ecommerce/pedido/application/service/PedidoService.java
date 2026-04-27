package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoPageResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

public interface PedidoService {

  PedidoResponse cadastraPedido(PedidoRequest pedidoRequest);

  PedidoResponse buscaPedidoPorId(UUID idPedido);

  PedidoPageResponse buscaPedidosDoCliente(
      UUID idCliente, StatusPedido statusPedido, int page, int size);

  List<ProdutoMaisVendidoResponse> geraRelatorioProdutosMaisVendidos(
      LocalDateTime dataInicio, LocalDateTime dataFim, Integer limite);
}
