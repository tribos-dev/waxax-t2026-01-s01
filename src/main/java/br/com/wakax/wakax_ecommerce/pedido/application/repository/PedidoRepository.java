package br.com.wakax.wakax_ecommerce.pedido.application.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

public interface PedidoRepository {

  Pedido salva(Pedido pedido);

  Pedido buscaPedidoPorId(UUID idPedido);

  Page<Pedido> buscaPedidosDoClientePaginado(
      UUID idCliente, StatusPedido statusPedido, Pageable pageable);

  List<ProdutoMaisVendidoResponse> buscaProdutosMaisVendidos(
      LocalDateTime dataInicio, LocalDateTime dataFim, Pageable limite);
}
