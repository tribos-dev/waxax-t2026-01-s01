package br.com.wakax.wakax_ecommerce.cliente.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.*;
import org.springframework.http.HttpStatus;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

  @Id @GeneratedValue private UUID id;

  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(nullable = false, unique = true)
  @NotNull
  private Pessoa pessoa;

  @Column(nullable = false, name = "data_criacao")
  @NotNull
  private LocalDateTime dataCriacao;

  @Column(nullable = false, name = "data_edicao")
  @NotNull
  private LocalDateTime dataEdicao;

  @PrePersist
  protected void onCreate() {
    dataCriacao = LocalDateTime.now();
    dataEdicao = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    dataEdicao = LocalDateTime.now();
  }

  public Cliente(ClienteRequest request) {
    this.pessoa = Pessoa.criarDe(request);
  }

  public void desativar(Pessoa pessoa) {
    pessoa.desativar();
    this.setDataEdicao(LocalDateTime.now());
  }

  public void validaClienteAtivo() {
    if (this.pessoa.getStatus() == StatusPessoa.INATIVO) {
      throw APIException.build(
              HttpStatus.CONFLICT,
              "Cliente está inativo e não pode realizar pagamentos");
    }
    
  public void alterar(ClienteAtualizaRequest request) {
    this.pessoa.alterar(request);
    this.dataEdicao = LocalDateTime.now();
  }
}
