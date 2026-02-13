package br.com.wakax.wakax_ecommerce.pedido.application.repository;

import java.util.Optional;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.Rastreamento;

public interface RastreamentoRepository {
  Rastreamento salva(Rastreamento rastreamento);

  Rastreamento buscaRastreamentoPorPedidoId(UUID idPedido);

  Optional<Rastreamento> buscaRastreamentoPorPedidoIdOptional(UUID idPedido);

  //Optional<RastreamentoResponse> consultaRastreamento(UUID idPedido);
}
