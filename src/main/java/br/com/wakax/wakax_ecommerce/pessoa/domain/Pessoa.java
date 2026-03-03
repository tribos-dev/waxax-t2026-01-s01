package br.com.wakax.wakax_ecommerce.pessoa.domain;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pessoa.application.api.request.DadosPessoa;
import br.com.wakax.wakax_ecommerce.pessoa.application.api.request.PessoaRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

  @Id @GeneratedValue private UUID id;

  @Column(length = 150, nullable = false)
  @NotNull
  @Size(max = 150)
  private String nome;

  @Column(length = 20, nullable = false, unique = true)
  @NotNull
  @Size(max = 20)
  private String cpfCnpj;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "pessoa_emails", joinColumns = @JoinColumn(name = "pessoa_id"))
  @Column(name = "emails", length = 255, nullable = false)
  private List<@NotNull @Size(max = 255) String> emails;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "pessoa_telefones", joinColumns = @JoinColumn(name = "pessoa_id"))
  @Column(name = "telefones", length = 30, nullable = false)
  private List<@NotNull @Size(max = 30) String> telefones;

  @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Endereco> enderecos;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private StatusPessoa status;

  public Pessoa(PessoaRequest pessoaRequest) {
    this.nome = pessoaRequest.getNome();
    this.cpfCnpj = pessoaRequest.getDocumento();
    this.emails = pessoaRequest.getEmails();
    this.telefones = pessoaRequest.getTelefones();
    this.status = StatusPessoa.ATIVO;
    this.enderecos = pessoaRequest.getEnderecos();
    vincularEnderecos();
  }

  public static Pessoa criarDe(DadosPessoa dadosPessoa) {
    Pessoa pessoa = new Pessoa();
    pessoa.nome = dadosPessoa.getNome();
    pessoa.cpfCnpj = dadosPessoa.getDocumento();
    pessoa.emails = dadosPessoa.getEmails();
    pessoa.telefones = dadosPessoa.getTelefones();
    pessoa.status = StatusPessoa.ATIVO;
    pessoa.enderecos = dadosPessoa.getEnderecos();
    pessoa.vincularEnderecos();
    return pessoa;
  }

  private void vincularEnderecos() {
    if (this.enderecos != null) {
      this.enderecos.forEach(endereco -> endereco.setPessoa(this));
    }
  }

  public void alterar(ClienteAtualizaRequest request) {

    if (request.getNome() != null && !request.getNome().isBlank()) {
      this.nome = request.getNome();
    }

    if (request.getEmailNovo() != null && request.getEmailAntigo() != null) {
      validarExistenciaEmail(request.getEmailAntigo());
      int index = this.emails.indexOf(request.getEmailAntigo());
      this.emails.set(index, request.getEmailNovo());
    }

    if (request.getTelefoneNovo() != null && request.getTelefoneAntigo() != null) {
      validarExistenciaTelefone(request.getTelefoneAntigo());
      int index = this.telefones.indexOf(request.getTelefoneAntigo());
      this.telefones.set(index, request.getTelefoneNovo());
    }

    if (request.getEnderecos() != null && !request.getEnderecos().isEmpty()) {
      request
          .getEnderecos()
          .forEach(
              novoEndereco -> {
                if (novoEndereco.isPrincipal()) {
                  desmarcarEnderecoPrincipalAtual();
                }
                novoEndereco.setPessoa(this);
                this.enderecos.add(novoEndereco);
              });
    }
  }

  private void validarExistenciaTelefone(String telefoneAntigo) {
    if (!this.telefones.contains(telefoneAntigo)) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.TELEFONE_INFORMADO_NAO_ENCONTRADO);
    }
  }

  private void validarExistenciaEmail(@Email String email) {
    if (!this.emails.contains(email)) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_INFORMADO_NAO_ENCONTRADO);
    }
  }

  public void adicionarEndereco(Endereco novoEndereco) {
    if (novoEndereco.isPrincipal()) {
      desmarcarEnderecoPrincipalAtual();
    }
    this.enderecos.add(novoEndereco);
  }

  private void desmarcarEnderecoPrincipalAtual() {
    this.enderecos.stream().filter(Endereco::isPrincipal).forEach(e -> e.setPrincipal(false));
  }
}
