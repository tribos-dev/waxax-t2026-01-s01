package br.com.wakax.wakax_ecommerce.pagamento.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.*;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.EstornaPagamentoRequest;
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import lombok.*;

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

  @Column private String motivoCancelamento;

  @Column(nullable = false)
  @Builder.Default
  @NotNull
  private int tentativasPagamento = 0;

  private static final int MAX_TENTATIVAS = 3;

  private LocalDateTime dataConfirmacao;

  @Column
  private LocalDateTime dataEstorno;

  @Column
  private String motivoEstorno;

  public Pagamento(Pedido pedido) {
    this.pedido = pedido;
    this.statusPagamento = StatusPagamento.AGUARDANDO;
    this.dataPagamento = LocalDateTime.now();
    this.valor = pedido.getValorTotal();
  }

  public void mudaStatusParaFalhou(CancelaPagamentoRequest request) {
    validaStatusPagamento();
    this.statusPagamento = StatusPagamento.FALHOU;
    this.motivoCancelamento = request.getMotivoCancelamento();
  }

  private void validaStatusPagamento() {
    if (this.statusPagamento == StatusPagamento.PAGO) {
      throw new APIException(HttpStatus.CONFLICT, ErrorCode.PAGAMENTO_JA_PROCESSADO);
    }
  }

  public void confirmarPagamento() {
    this.statusPagamento = StatusPagamento.PAGO;
    this.dataConfirmacao = LocalDateTime.now();
  }

  public void aguardarPagamento() {
    this.statusPagamento = StatusPagamento.AGUARDANDO;
  }

  public void prepararReprocessamento() {
    validarPagamentoJaProcessado();
    validarStatusParaReprocessamento();
    validarLimiteTentativas();
    this.tentativasPagamento++;
    this.statusPagamento = StatusPagamento.AGUARDANDO;
    this.dataPagamento = LocalDateTime.now();
  }

  public void validarPagamentoJaProcessado() {
    if (this.statusPagamento == StatusPagamento.PAGO) {
      throw new APIException(
          HttpStatus.CONFLICT,
          ErrorCode.PAGAMENTO_JA_PROCESSADO_COM_SUCESSO,
          this.getStatusPagamento());
    }
  }

  public void validarStatusParaReprocessamento() {
    if (this.statusPagamento != StatusPagamento.FALHOU) {
      throw new APIException(
          HttpStatus.CONFLICT,
          ErrorCode.PAGAMENTO_NAO_PODE_SER_REPROCESSADO,
          this.getStatusPagamento());
    }
  }

  public void validarLimiteTentativas() {
    if (this.tentativasPagamento >= MAX_TENTATIVAS) {
      throw new APIException(
          HttpStatus.CONFLICT, ErrorCode.LIMITE_DE_TENTATIVAS_EXCEDIDO, this.getStatusPagamento());
    }
  }


  public void prepararEstorno(EstornaPagamentoRequest estornaPagamentoRequest) {
    String motivo = estornaPagamentoRequest.getMotivoEstorno();
    validarStatusParaEstorno();
    validarMotivoParaEstorno(motivo);
    this.dataEstorno = LocalDateTime.now();
    this.statusPagamento = StatusPagamento.ESTORNADO;
    this.motivoEstorno = estornaPagamentoRequest.getMotivoEstorno();
  }




  public void validarStatusParaEstorno() {
    if (this.statusPagamento != StatusPagamento.PAGO) {
      throw new APIException(
              HttpStatus.CONFLICT,
              ErrorCode.PAGAMENTO_NAO_PODE_SER_ESTORNADO,
              this.getStatusPagamento());
    }
  }

  public void validarMotivoParaEstorno(String motivo) {
    if (motivo == null || motivo.isBlank()) {
      throw new APIException(
              HttpStatus.BAD_REQUEST,
              ErrorCode.MOTIVO_ESTORNO_OBRIGATORIO
      );
    }
  }
}
