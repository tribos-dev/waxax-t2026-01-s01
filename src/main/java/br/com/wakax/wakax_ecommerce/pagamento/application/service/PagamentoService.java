package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PagamentoService {

  PagamentoResponse processaPagamento(PagamentoRequest novoPagamento);

  PagamentoResponse buscaPagamentoPorId(UUID idPagamento);

    PagamentoPageResponse buscaPagamentosPaginado(String status, int page, int size);
}
