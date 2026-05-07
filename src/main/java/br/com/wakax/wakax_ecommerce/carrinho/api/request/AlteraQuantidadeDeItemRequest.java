package br.com.wakax.wakax_ecommerce.carrinho.api.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class AlteraQuantidadeDeItemRequest {
    @NotNull(message = "{validacao.alteraquantidadeitem.quantidade.obrigatoria}")
    @Min(value = 1, message = "{item-carrinho.quantidade.menor.que.um}")
    private Integer quantidade;
}
