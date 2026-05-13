package br.com.wakax.wakax_ecommerce.pedido.application.api.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelamentoPedidoRequest {
  @NotBlank private String motivoCancelamento;
}
