package br.com.wakax.wakax_ecommerce.estoque.api.request;

import javax.validation.constraints.Positive;

public record RemoveEstoqueRequest(
    @Positive(message = "A quantidade a ser removida deve ser maior que zero")
        Integer quantidade) {}
