package br.com.wakax.wakax_ecommerce.produto.api;

import javax.validation.constraints.NotNull;

import br.com.wakax.wakax_ecommerce.produto.domain.StatusProduto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ProdutoAlteraStatusRequest {

  @NotNull(message = "Status é obrigatório")
  private StatusProduto status;
  private String motivo;
}
