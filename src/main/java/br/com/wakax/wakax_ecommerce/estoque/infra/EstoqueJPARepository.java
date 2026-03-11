package br.com.wakax.wakax_ecommerce.estoque.infra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;

public interface EstoqueJPARepository extends JpaRepository<Estoque, UUID> {
  Optional<Estoque> findByProdutoId(UUID produtoId);

  List<Estoque> findByQuantidadeDisponivelGreaterThanEqual(Integer quantidadeMinima);

  List<Estoque> findByQuantidadeDisponivelEquals(Integer quantidade);
}
