package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class ClienteEnderecoRequest {
  @Size(max = 150)
  private String logradouro;

  @Size(max = 20)
  private String numero;

  @Size(max = 100)
  private String complemento;

  @Size(max = 100)
  private String bairro;

  @Size(max = 100)
  private String cidade;

  @Size(max = 50)
  private String estado;

  @Size(max = 20)
  private String cep;

  @NotNull private boolean principal;
}
