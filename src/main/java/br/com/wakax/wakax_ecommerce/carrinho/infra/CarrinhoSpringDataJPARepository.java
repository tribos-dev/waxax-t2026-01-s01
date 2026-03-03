package br.com.wakax.wakax_ecommerce.carrinho.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.StatusCarrinho;
import org.springframework.data.jpa.repository.Query;

public interface CarrinhoSpringDataJPARepository extends JpaRepository<Carrinho, UUID> {

  Carrinho findByClienteIdAndStatusCarrinho(UUID idCliente, StatusCarrinho status);

  List<Carrinho> findAllByClienteIdOrderByDataCriacaoDesc(UUID idCliente);

  @Query("""
       select count(c) > 0
       from Carrinho c
       join c.cliente cl
       join cl.pessoa p
       join p.emails e
       where c.id = :idCarrinho
       and lower(e) = lower(:email)
       """)
  boolean existsByIdAndEmail(UUID idCarrinho, String email);
}
