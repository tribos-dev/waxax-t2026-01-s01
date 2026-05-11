package br.com.wakax.wakax_ecommerce.pedido.application.api.request;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoEntregaRequest {

  @NotNull(message = "O id do endereço de entrega não pode ser nulo")
  private UUID idEnderecoEntrega;
}
