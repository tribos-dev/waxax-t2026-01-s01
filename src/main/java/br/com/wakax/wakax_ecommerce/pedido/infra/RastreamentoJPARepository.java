package br.com.wakax.wakax_ecommerce.pedido.infra;

import java.util.Optional;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wakax.wakax_ecommerce.pedido.domain.Rastreamento;
import org.springframework.data.jpa.repository.Query;

public interface RastreamentoJPARepository extends JpaRepository<Rastreamento, UUID> {

  @EntityGraph(attributePaths = {"historico"})
  Optional<Rastreamento> findByPedidoId(UUID pedidoId);

    @Query("SELECT r FROM Rastreamento r " +
            "JOIN FETCH r.pedido p " +
            "WHERE p.id = :idPedido "
    )
  Optional<RastreamentoResponse> findAllRastreamentoPorIdPedido(UUID idPedido);
}
