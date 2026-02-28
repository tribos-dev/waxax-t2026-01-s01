package br.com.wakax.wakax_ecommerce.cliente.application.api.response;

import java.time.LocalDateTime;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ClienteAtualizaResponse {
  private String nome;
  private String email;
  private Endereco endereco;
  private StatusPessoa statusPessoa;
  private LocalDateTime dataEdicao;

  public ClienteAtualizaResponse(Cliente cliente, ClienteAtualizaRequest request) {
    this.nome = cliente.getPessoa().getNome();
    this.dataEdicao = cliente.getDataEdicao();
    this.statusPessoa = cliente.getPessoa().getStatus();

    if (request.getEmails() != null && !request.getEmails().isEmpty()) {

      this.email = request.getEmails().get(request.getEmails().size() - 1);
    }

    if (request.getEnderecos() != null && !request.getEnderecos().isEmpty()) {

      this.endereco = request.getEnderecos().get(request.getEnderecos().size() - 1);
    }
  }
}
