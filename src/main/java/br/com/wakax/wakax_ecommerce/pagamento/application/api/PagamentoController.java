package br.com.wakax.wakax_ecommerce.pagamento.application.api;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.ReprocessarPagamentoResponse;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPedidoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.service.PagamentoService;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class PagamentoController implements PagamentoAPI {

  private final PagamentoService pagamentoService;

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
  public PagamentoPageResponse buscaPagamentosPaginado(
      StatusPagamento statusPagamento, int page, int size) {
    log.debug("[start] PagamentoController - buscaPagamentosPaginado");
    PagamentoPageResponse pagamentoPageResponse =
        pagamentoService.buscaPagamentosPaginado(statusPagamento, page, size);
    log.debug("[finish] PagamentoController - buscaPagamentosPaginado");
    return pagamentoPageResponse;
  }

  @Override
  public PagamentoPedidoResponse buscaPagamentoPorIdPedido(UUID idPedido) {
    log.debug("[start] PagamentoController - buscaPagamentoPorIdPedido");
    PagamentoPedidoResponse response = pagamentoService.buscaPagamentoPorIdPedido(idPedido);
    log.debug("[finish] PagamentoController - buscaPagamentoPorIdPedido");
    return response;
  }

  @Override
  public void cancelaPagamento(UUID idPagamento, CancelaPagamentoRequest cancelaPagamentoRequest) {
    log.info("[start] PagamentoController - cancelaPagamento");
    pagamentoService.cancelaPagamento(idPagamento, cancelaPagamentoRequest);
    log.info("[finish] PagamentoController - cancelaPagamento");
  }

  @Override
  public ReprocessarPagamentoResponse reprocessaPagamento(UUID idPagamento) {
    log.info("[start] PagamentoController - reprocessaPagamento");
    ReprocessarPagamentoResponse response = pagamentoService.reprocessaPagamento(idPagamento);
    log.debug("[finish] PagamentoController - reprocessaPagamento");
    return response;
  }

  @Override
  public PagamentoResponse confirmarPagamento(UUID idPagamento) {
    log.debug("[start] PagamentoController - confirmarPagamento");
    PagamentoResponse response = pagamentoService.confirmarPagamento(idPagamento);
    log.debug("[finish] PagamentoController - confirmarPagamento");
    return response;
  }
}
