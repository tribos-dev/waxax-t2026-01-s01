package br.com.wakax.wakax_ecommerce.estoque.application.service;

import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstoqueMapper {

    public EstoqueListagemResponse toResponse(List<Estoque> estoques, BigDecimal valorTotal) {
        List<EstoqueResponse> itens = estoques.stream()
                .map(EstoqueResponse::new)
                .collect(Collectors.toList());

        return EstoqueListagemResponse.builder()
                .itens(itens)
                .valorTotalInventario(valorTotal)
                .totalItens(estoques.size())
                .build();
    }
}
