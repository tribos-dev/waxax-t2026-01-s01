package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import lombok.Getter;

@Getter
public class EstornarPagamentoResponse {
  private final UUID idPagamento;
  private final UUID pedidoId;
  private final StatusPagamento statusPagamento;
  private final LocalDateTime dataEstorno;
  private final String motivoEstorno;
  private final String mensagem;

  public EstornarPagamentoResponse(Pagamento pagamento) {
    this.idPagamento = pagamento.getId();
    this.pedidoId = pagamento.getPedido().getId();
    this.statusPagamento = pagamento.getStatusPagamento();
    this.dataEstorno = pagamento.getDataEstorno();
    this.motivoEstorno = pagamento.getMotivoEstorno();
    this.mensagem = "Pagamento estornado";
  }
}
