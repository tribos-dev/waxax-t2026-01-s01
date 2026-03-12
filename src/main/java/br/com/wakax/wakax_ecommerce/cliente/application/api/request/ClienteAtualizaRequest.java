package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class ClienteAtualizaRequest {
  @NotBlank
  @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  private List<@Email @NotBlank(message = "Email inválido") @Size(max = 150) String> emails;

  private List<@NotBlank @Size(max = 15) String> telefones;

  private List<ClienteEnderecoRequest> enderecos;
}
