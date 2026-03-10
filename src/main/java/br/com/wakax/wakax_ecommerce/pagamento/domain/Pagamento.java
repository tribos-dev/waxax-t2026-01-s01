package br.com.wakax.wakax_ecommerce.pagamento.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.*;

import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import lombok.*;
import org.springframework.http.HttpStatus;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {
  @Id @GeneratedValue private UUID id;

  @OneToOne(optional = false)
  @JoinColumn(nullable = false, unique = true)
  @NotNull
  private Pedido pedido;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private StatusPagamento statusPagamento;

  @Column(nullable = false)
  @NotNull
  private LocalDateTime dataPagamento;

  @Column(nullable = false)
  @NotNull
  @PositiveOrZero
  private BigDecimal valor;

  @Column(nullable = false)
  @Builder.Default
  @NotNull
  private int tentativasPagamento = 0;

  private static final int MAX_TENTATIVAS = 3;

  public Pagamento(Pedido pedido) {
    this.pedido = pedido;
    this.statusPagamento = StatusPagamento.AGUARDANDO;
    this.dataPagamento = LocalDateTime.now();
    this.valor = pedido.getValorTotal();
  }

  public void confirmarPagamento() {
    this.statusPagamento = StatusPagamento.PAGO;
  }

  public void aguardarPagamento() {
    this.statusPagamento = StatusPagamento.AGUARDANDO;
  }

  public void prepararReprocessamento() {

    if (this.statusPagamento == StatusPagamento.PAGO) {
      throw new APIException(
              HttpStatus.CONFLICT,
              ErrorCode.PAGAMENTO_JA_PROCESSADO_COM_SUCESSO,
              this.getStatusPagamento());
    }
    if (this.statusPagamento != StatusPagamento.FALHOU) {
      throw new APIException(
              HttpStatus.CONFLICT,
              ErrorCode.PAGAMENTO_NAO_PODE_SER_REPROCESSADO,
              this.getStatusPagamento());
    }
    if (this.tentativasPagamento >= MAX_TENTATIVAS) {
      throw new APIException(
              HttpStatus.CONFLICT,
              ErrorCode.LIMITE_DE_TENTATIVAS_EXCEDIDO,
              this.getStatusPagamento());
    }
    this.tentativasPagamento++;
    this.statusPagamento = StatusPagamento.AGUARDANDO;
    this.dataPagamento = LocalDateTime.now();
  }
}
