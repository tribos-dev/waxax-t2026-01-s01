package br.com.wakax.wakax_ecommerce.cliente.application.api.response;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import lombok.Getter;

@Getter
public class EnderecoClienteResponse {

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
  @Size(max = 20)
  private String cep;

  @NotNull private boolean principal;

  public EnderecoClienteResponse(Endereco endereco) {
    this.logradouro = endereco.getLogradouro();
    this.numero = endereco.getNumero();
    this.complemento = endereco.getComplemento();
    this.bairro = endereco.getBairro();
    this.cep = endereco.getCep();
    this.principal = endereco.isPrincipal();
  }
}
