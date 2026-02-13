package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import lombok.Getter;

@Getter
public class PagamentoPedidoResponse {
  private final StatusPagamento statusPagamento;
  private final BigDecimal valor;
  private final LocalDateTime dataPagamento;
  private final FormaPagamento metodoPagamento;

  public PagamentoPedidoResponse(Pagamento pagamento) {
    this.statusPagamento = pagamento.getStatusPagamento();
    this.valor = pagamento.getValor();
    this.dataPagamento = pagamento.getDataPagamento();
    this.metodoPagamento = pagamento.getPedido().getFormaPagamento();
  }
}
