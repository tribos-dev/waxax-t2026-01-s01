package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPedidoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;

public interface PagamentoService {

  PagamentoResponse processaPagamento(PagamentoRequest novoPagamento);

  PagamentoResponse buscaPagamentoPorId(UUID idPagamento);

  PagamentoResponse confirmarPagamento(UUID idPagamento);

  PagamentoPageResponse buscaPagamentosPaginado(
      StatusPagamento statusPagamento, int page, int size);

  PagamentoPedidoResponse buscaPagamentoPorIdPedido(UUID idPedido);
}
