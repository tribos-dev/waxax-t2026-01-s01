package br.com.wakax.wakax_ecommerce.carrinho.api.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlteraQuantidadeDeItemRequest {
  @NotNull(message = "{validacao.alteraquantidadeitem.quantidade.obrigatoria}")
  @Min(value = 1, message = "{item-carrinho.quantidade.menor.que.um}")
  private Integer quantidade;
}
