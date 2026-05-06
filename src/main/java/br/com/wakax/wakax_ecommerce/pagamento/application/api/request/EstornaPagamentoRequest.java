package br.com.wakax.wakax_ecommerce.pagamento.application.api.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstornaPagamentoRequest {

    @NotBlank private String motivoEstorno;
}
