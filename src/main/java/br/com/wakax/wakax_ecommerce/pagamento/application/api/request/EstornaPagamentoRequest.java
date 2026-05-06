package br.com.wakax.wakax_ecommerce.pagamento.application.api.request;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstornaPagamentoRequest {

  @NotBlank private String motivoEstorno;
}
