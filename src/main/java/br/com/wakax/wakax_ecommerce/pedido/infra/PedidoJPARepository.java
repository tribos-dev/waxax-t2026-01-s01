package br.com.wakax.wakax_ecommerce.pedido.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

public interface PedidoJPARepository extends JpaRepository<Pedido, UUID> {

  @EntityGraph(
      attributePaths = {
        "itensPedido",
        "itensPedido.produto",
        "cliente",
        "cliente.pessoa",
        "enderecoEntrega"
      })
  Optional<Pedido> findById(UUID id);

  @Query(
      """
            SELECT p FROM Pedido p WHERE p.cliente.id = :idCliente AND (:statusPedido IS NULL OR p.status = :statusPedido)
            """)
  Page<Pedido> findAllPedidosPageableByIdClienteWithStatusPedido(
      UUID idCliente, StatusPedido statusPedido, Pageable pageable);
}
