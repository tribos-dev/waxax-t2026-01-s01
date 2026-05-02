package br.com.wakax.wakax_ecommerce.produto.api;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ProdutoStatusRequest {

  @NotBlank private String status;
  private String motivo;
}
