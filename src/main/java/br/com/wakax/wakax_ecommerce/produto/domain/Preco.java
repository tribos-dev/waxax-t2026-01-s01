package br.com.wakax.wakax_ecommerce.produto.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
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
    if (this.valor.compareTo(novoValor) == 0) {
      throw new APIException(
          HttpStatus.BAD_REQUEST, ErrorCode.PRECO_JA_CADASTRADO, produto.getId());
    }

    this.valor = novoValor;
  }

  public Preco(TipoPreco tipo, BigDecimal valor, Produto produto) {
    this.tipo = tipo;
    this.valor = valor;
    this.produto = produto;
  }
}
