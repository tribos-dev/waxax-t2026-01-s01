package br.com.wakax.wakax_ecommerce.produto.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import br.com.wakax.wakax_ecommerce.auth.usuario.domain.Usuario;
import lombok.*;

@Entity
@Table(name = "historico_atualizacao_produto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoAtualizacaoProduto {
  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "produto_id", nullable = false)
  @NotNull
  private Produto produto;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id", nullable = false, referencedColumnName = "idUsuario")
  @NotNull
  private Usuario usuario;

  @Column(name = "data_hora", nullable = false)
  @NotNull
  private LocalDateTime dataHora;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_preco")
  private TipoPreco tipoPreco;

  @Column(name = "valor_anterior")
  private BigDecimal valorAnterior;

  @Column(name = "valor_novo")
  private BigDecimal valorNovo;

  @Column(name = "motivo")
  private String motivo;

  public static HistoricoAtualizacaoProduto deAtualizacao(Produto produto, Usuario usuario) {
    return HistoricoAtualizacaoProduto.builder()
        .produto(produto)
        .usuario(usuario)
        .dataHora(LocalDateTime.now())
        .build();
  }

  public static HistoricoAtualizacaoProduto deAtualizacaoPreco(
      Produto produto,
      Usuario usuario,
      TipoPreco tipoPreco,
      BigDecimal valorAnterior,
      BigDecimal valorNovo,
      String motivo) {
    return HistoricoAtualizacaoProduto.builder()
        .produto(produto)
        .usuario(usuario)
        .dataHora(LocalDateTime.now())
        .tipoPreco(tipoPreco)
        .valorAnterior(valorAnterior)
        .valorNovo(valorNovo)
        .motivo(motivo)
        .build();
  }
}
