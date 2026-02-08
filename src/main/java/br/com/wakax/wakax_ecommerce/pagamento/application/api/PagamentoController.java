package br.com.wakax.wakax_ecommerce.pagamento.application.api;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pagamento.infra.PagamentoInfraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class PagamentoController implements PagamentoAPI {

  private final PagamentoService pagamentoService;
  private PagamentoInfraRepository pagamentoInfraRepository;

  @Override
  public PagamentoResponse processaPagamento(PagamentoRequest novoPagamento) {
    log.debug("[start] PagamentoController - criaPagamento");
    PagamentoResponse response = pagamentoService.processaPagamento(novoPagamento);
    log.debug("[finish] PagamentoController - criaPagamento");
    return response;
  }

  @Override
  public PagamentoResponse buscaPagamentoPorId(UUID idPagamento) {
    log.debug("[start] PagamentoController - buscaPagamentoPorId");
    PagamentoResponse response = pagamentoService.buscaPagamentoPorId(idPagamento);
    log.debug("[finish] PagamentoController - buscaPagamentoPorId");
    return response;
  }

    @Override
    public PagamentoPageResponse buscaPagamentosPaginado(String status, int page, int size) {
        log.info("[start] PagamentoController - buscaPagamentosPaginado");

        StatusPagamento statusPagamento = null;
        if (status != null && !status.isBlank()) {
            statusPagamento = StatusPagamento.valueOf(status.toUpperCase());
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPagamento").descending());
        Page<Pagamento> pagamentos = pagamentoService.buscaPagamentosPaginado(statusPagamento, pageable);
        log.info("[finish] PagamentoController - buscaPagamentosPaginado");
        return PagamentoPageResponse.convertePaginado(
                pagamentos.getContent(),
                pagamentos.getTotalElements(),
                pagamentos.getTotalPages()
        );
    }


}
