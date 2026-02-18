package br.com.wakax.wakax_ecommerce.pagamento.application.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;

public interface PagamentoRepository {

  Pagamento salva(Pagamento pagamento);

  Pagamento buscaPagamentoPorId(UUID idPagamento);

  Optional<Pagamento> buscaPagamentoPorPedidoId(UUID pedidoId);

  Page<Pagamento> buscaPagamentosPaginado(StatusPagamento statusPagamento, Pageable pageable);
}
