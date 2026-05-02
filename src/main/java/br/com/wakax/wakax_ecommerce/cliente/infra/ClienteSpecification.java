package br.com.wakax.wakax_ecommerce.cliente.infra;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;

public class ClienteSpecification {

    public static Specification<Cliente> comFiltros(String cpf, String email, String nome) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<?, ?> pessoa = root.join("pessoa");

            if (cpf != null && !cpf.isBlank()) {
                predicates.add(cb.equal(pessoa.get("cpfCnpj"), cpf));
            }
            if (email != null && !email.isBlank()) {
                predicates.add(cb.equal(cb.lower(pessoa.join("emails")), email.toLowerCase()));
            }
            if (nome != null && !nome.isBlank()) {
                predicates.add(cb.like(cb.lower(pessoa.get("nome")), "%" + nome.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
