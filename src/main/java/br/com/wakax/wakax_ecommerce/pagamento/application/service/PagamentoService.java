package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPedidoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.ReprocessarPagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;

public interface PagamentoService {

  PagamentoResponse processaPagamento(PagamentoRequest novoPagamento);

  PagamentoResponse buscaPagamentoPorId(UUID idPagamento);

  PagamentoPageResponse buscaPagamentosPaginado(
      StatusPagamento statusPagamento, int page, int size);

  PagamentoPedidoResponse buscaPagamentoPorIdPedido(UUID idPedido);

  void cancelaPagamento(UUID idPagamento, CancelaPagamentoRequest cancelaPagamentoRequest);

  ReprocessarPagamentoResponse reprocessaPagamento(UUID idPagamento);
}
