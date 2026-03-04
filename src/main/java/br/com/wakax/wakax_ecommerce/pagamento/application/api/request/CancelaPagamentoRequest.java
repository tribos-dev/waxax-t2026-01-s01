package br.com.wakax.wakax_ecommerce.pagamento.application.api.request;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelaPagamentoRequest {

    @NotNull(message = "{validacao.pedido}")
    private UUID pedidoId;

    @NotBlank
    private String motivoCancelamento;
}
