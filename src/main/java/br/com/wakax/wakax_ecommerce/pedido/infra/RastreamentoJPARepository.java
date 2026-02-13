package br.com.wakax.wakax_ecommerce.pedido.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.Rastreamento;

public interface RastreamentoJPARepository extends JpaRepository<Rastreamento, UUID> {

  @EntityGraph(attributePaths = {"historico"})
  Optional<Rastreamento> findByPedidoId(UUID pedidoId);
}
