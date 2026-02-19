package br.com.wakax.wakax_ecommerce.estoque.application.service;

import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;

import java.util.Comparator;
import java.util.List;

public class EstoqueFiltro {
    public static List<Estoque> aplicar(
            List<Estoque> estoques, Integer quantidadeMinima, Boolean apenasEmFalta) {
        return estoques.stream()
                .filter(e -> quantidadeMinima == null || e.getQuantidadeDisponivel() >= quantidadeMinima)
                .filter(e -> !Boolean.TRUE.equals(apenasEmFalta) || e.getQuantidadeDisponivel() == 0)
                .sorted(Comparator.comparing(e -> e.getProduto().getDescricao())).toList();
    }
}
