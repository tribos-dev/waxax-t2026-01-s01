package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import lombok.Getter;

@Getter
public class ReprocessarPagamentoResponse {

  private final UUID idPagamento;
  private final UUID pedidoId;
  private final StatusPagamento statusPagamento;
  private final LocalDateTime dataPagamento;
  private final BigDecimal valor;
  private final FormaPagamento metodoPagamento;
  private final Integer tentativas;
  private final String mensagem;

  public ReprocessarPagamentoResponse(Pagamento pagamento) {
    this.idPagamento = pagamento.getId();
    this.pedidoId = pagamento.getPedido().getId();
    this.statusPagamento = pagamento.getStatusPagamento();
    this.dataPagamento = pagamento.getDataPagamento();
    this.valor = pagamento.getValor();
    this.metodoPagamento = pagamento.getPedido().getFormaPagamento();
    this.tentativas = pagamento.getTentativasPagamento();
    this.mensagem = "Pagamento enviado para nova tentativa";
  }
}
