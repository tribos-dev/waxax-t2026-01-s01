package br.com.wakax.wakax_ecommerce.cliente.infra;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import org.springframework.data.jpa.repository.Query;

public interface ClienteSpringDataJpaRepository extends JpaRepository<Cliente, UUID> {

    @EntityGraph(attributePaths = {
            "pessoa",
            "pessoa.emails"
    })
    @Query("""
    SELECT c
    FROM Cliente c
""")
    Page<Cliente> buscaTodosOsClientes(Pageable pageable);


}
