package br.com.wakax.wakax_ecommerce.pagamento.infra;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoJPARepository extends JpaRepository<Pagamento, UUID> {

  @Query("SELECT p FROM Pagamento p JOIN FETCH p.pedido WHERE p.id = :idPagamento")
  java.util.Optional<Pagamento> findByIdComPedido(@Param("idPagamento") UUID idPagamento);

  @Query("SELECT p FROM Pagamento p WHERE p.pedido.id = :idPedido")
  Optional<Pagamento> findByPedidoId(@Param("idPedido") UUID idPedido);

  @Query("SELECT p FROM Pagamento p " +
         "WHERE (:statusPagamento IS NULL OR p.statusPagamento = :statusPagamento)")
  Page<Pagamento> findAllPagamentosPaginado(StatusPagamento statusPagamento, Pageable pageable);

  /*@Query ("SELECT SUM(p.valor) FROM Pagamento p " +
          "       WHERE (:status IS NULL OR p.statusPagamento = :status)")
  BigDecimal findAllSomaPagamentosFiltro(StatusPagamento statusPagamento);*/
}
