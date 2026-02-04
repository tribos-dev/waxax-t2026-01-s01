package br.com.wakax.wakax_ecommerce.produto.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

public interface ProdutoJPARepository extends JpaRepository<Produto, UUID> {
  @Query("SELECT p FROM Produto p LEFT JOIN FETCH p.precos WHERE p.id = :id")
  Optional<Produto> findByIdComPrecos(@Param("id") UUID id);
  
  @Query("SELECT DISTINCT p FROM Produto p LEFT JOIN FETCH p.precos")
  List<Produto> findAllComPrecos();
  
  @Query(value = "SELECT DISTINCT p FROM Produto p LEFT JOIN FETCH p.precos",
         countQuery = "SELECT COUNT(DISTINCT p) FROM Produto p")
  Page<Produto> findAllComPrecosPaginado(Pageable pageable);

  boolean existsByDescricao(String descricao);
}
