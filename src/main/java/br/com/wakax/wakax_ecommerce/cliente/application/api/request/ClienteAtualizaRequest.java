package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class ClienteAtualizaRequest {
  @NotBlank
  @Size(max = 150)
  private String nome;

  @NotEmpty private List<@Email @NotBlank @Size(max = 150) String> emails;

  @NotEmpty private List<@NotBlank @Size(max = 15) String> telefones;

  @NotEmpty @Valid private List<ClienteEnderecoRequest> enderecos;
}
