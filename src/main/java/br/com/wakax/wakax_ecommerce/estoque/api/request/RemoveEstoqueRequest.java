package br.com.wakax.wakax_ecommerce.estoque.api.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record RemoveEstoqueRequest(
        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade a ser removida deve ser maior que zero")
        Integer quantidade
) {

}
