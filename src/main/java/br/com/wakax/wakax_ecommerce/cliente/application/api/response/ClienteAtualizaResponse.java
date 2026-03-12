package br.com.wakax.wakax_ecommerce.cliente.application.api.response;

import java.time.LocalDateTime;
import java.util.List;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.Getter;

@Getter
public class ClienteAtualizaResponse {
  private String nome;
  private List<String> emails;
  private List<String> telefones;
  private List<EnderecoClienteResponse> enderecos;
  private StatusPessoa status;
  private LocalDateTime dataAtualizacao;

  public ClienteAtualizaResponse(Cliente cliente) {

    this.nome = cliente.getPessoa().getNome();

    this.emails = cliente.getPessoa().getEmails().stream().map(e -> e.toString()).toList();

    this.telefones = cliente.getPessoa().getTelefones().stream().map(t -> t.toString()).toList();

    this.enderecos =
        cliente.getPessoa().getEnderecos().stream().map(EnderecoClienteResponse::new).toList();

    this.status = cliente.getPessoa().getStatus();
    this.dataAtualizacao = cliente.getDataEdicao();
  }
}
