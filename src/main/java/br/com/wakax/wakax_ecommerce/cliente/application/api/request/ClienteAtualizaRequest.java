package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import lombok.Getter;

@Getter
public class ClienteAtualizaRequest {
  @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
  private String nome;

  @Email private String emailAntigo;
  @Email private String emailNovo;

  private String telefoneAntigo;
  private String telefoneNovo;

  private List<Endereco> enderecos;
}
