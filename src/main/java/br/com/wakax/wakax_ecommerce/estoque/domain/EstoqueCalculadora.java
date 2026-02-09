package br.com.wakax.wakax_ecommerce.estoque.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class EstoqueCalculadora {
    public static BigDecimal valorTotal(List<Estoque> estoques) {
        return estoques.stream()
                .map(Estoque::getCustoTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
