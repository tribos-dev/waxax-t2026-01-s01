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
            "JOIN FETCH r.pedido p " +         // Conecta com Pedido
            "JOIN FETCH p.cliente c " +        // Conecta com Cliente
            "JOIN FETCH c.pessoa pes " +       // Conecta com Pessoa
            "JOIN pes.emails e " +             // Conecta com a lista de e-mails
            "WHERE p.id = :idPedido "
            //"AND e = :clientePorEmail"
    )
  Optional<RastreamentoResponse> findAllRastreamentoPorIdPedido(UUID idPedido);
}
