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

}
