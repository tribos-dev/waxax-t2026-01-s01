package br.com.wakax.wakax_ecommerce.pedido.application.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoEntregaRequest {

  @NotNull(message = "O id do endereço de entrega não pode ser nulo")
  private UUID idEnderecoEntrega;
}
