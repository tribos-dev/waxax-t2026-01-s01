package br.com.wakax.wakax_ecommerce.pedido.infra;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  @Query(
      """
          SELECT p.id AS produtoId,
                 p.descricao AS descricaoProduto,
                 SUM(ip.quantidade) AS quantidadeTotal,
                 SUM(ip.quantidade * ip.valorUnitario) AS receitaBruta
          FROM ItemPedido ip
          JOIN ip.produto p
          JOIN ip.pedido ped
          WHERE ped.dataPedido BETWEEN :dataInicio AND :dataFim
          GROUP BY p.id, p.descricao
          ORDER BY SUM(ip.quantidade) DESC
          """)
  Page<ProdutoMaisVendidoProjection> findProdutosMaisVendidos(
      @Param("dataInicio") LocalDateTime dataInicio,
      @Param("dataFim") LocalDateTime dataFim,
      Pageable pageable);
}
