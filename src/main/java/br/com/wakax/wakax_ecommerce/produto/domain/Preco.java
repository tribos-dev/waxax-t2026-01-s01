package br.com.wakax.wakax_ecommerce.produto.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preco {
  @Id @GeneratedValue private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private TipoPreco tipo;

  @Column(nullable = false)
  @NotNull
  @Positive
  private BigDecimal valor;

  @ManyToOne private Produto produto;

  @Column(name = "data_de_cadastro")
  private LocalDateTime dataDeCadastro;

  @Column(name = "data_de_atualizacao")
  private LocalDateTime dataDeAtualizacao;

  @PrePersist
  public void prePersist() {
    this.dataDeCadastro = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.dataDeAtualizacao = LocalDateTime.now();
  }

  public void atualizaValor(BigDecimal novoValor) {
    this.valor = novoValor;
  }

  public Preco(TipoPreco tipo, BigDecimal valor, Produto produto) {
    this.tipo = tipo;
    this.valor = valor;
    this.produto = produto;
  }
}
