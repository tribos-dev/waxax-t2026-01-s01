package br.com.wakax.wakax_ecommerce.produto.domain;

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
}
