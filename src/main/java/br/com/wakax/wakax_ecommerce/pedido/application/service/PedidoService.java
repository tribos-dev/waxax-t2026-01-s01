package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoPageResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.CancelamentoPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.StatusPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

public interface PedidoService {

  PedidoResponse cadastraPedido(PedidoRequest pedidoRequest);

  PedidoResponse buscaPedidoPorId(UUID idPedido);

  void atualizaStatusPedido(UUID idPedido, StatusPedidoRequest statusPedidoRequest);

  void cancelarPedido(UUID idPedido, CancelamentoPedidoRequest cancelamentoPedidoRequest);

  PedidoPageResponse buscaPedidosDoCliente(
      UUID idCliente, StatusPedido statusPedido, int page, int size);

  List<ProdutoMaisVendidoResponse> geraRelatorioProdutosMaisVendidos(
      LocalDateTime dataInicio, LocalDateTime dataFim, Integer limite);
}
