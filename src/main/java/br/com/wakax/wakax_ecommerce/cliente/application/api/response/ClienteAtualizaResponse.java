package br.com.wakax.wakax_ecommerce.cliente.application.api.response;

import java.time.LocalDateTime;
import java.util.List;

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
  private EnderecoClienteResponse endereco;
  private String telefone;
  private StatusPessoa statusPessoa;
  private LocalDateTime dataEdicao;

  public ClienteAtualizaResponse(Cliente cliente, ClienteAtualizaRequest request) {
    this.nome = cliente.getPessoa().getNome();
    this.dataEdicao = cliente.getDataEdicao();
    this.statusPessoa = cliente.getPessoa().getStatus();

    if (request.getTelefoneNovo() != null) {
      this.telefone = request.getTelefoneNovo();
    }

    if (request.getEmailNovo() != null) {
      this.email = request.getEmailNovo();
    }
    List<Endereco> enderecos = cliente.getPessoa().getEnderecos();
    if (enderecos != null && !enderecos.isEmpty()) {
      Endereco ultimo = enderecos.get(enderecos.size() - 1);
      this.endereco = new EnderecoClienteResponse(ultimo);
    }
  }
}
