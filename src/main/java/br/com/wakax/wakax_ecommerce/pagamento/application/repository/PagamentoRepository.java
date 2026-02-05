package br.com.wakax.wakax_ecommerce.pagamento.application.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import org.springframework.data.domain.Page;

public interface PagamentoRepository {

  Pagamento salva(Pagamento pagamento);

  Pagamento buscaPagamentoPorId(UUID idPagamento);

  Optional<Pagamento> buscaPagamentoPorPedidoId(UUID pedidoId);
}
