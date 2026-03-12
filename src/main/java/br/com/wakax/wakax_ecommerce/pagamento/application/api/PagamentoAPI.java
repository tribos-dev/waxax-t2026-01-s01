package br.com.wakax.wakax_ecommerce.pagamento.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPageResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoPedidoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.PagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.ReprocessarPagamentoResponse;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;

@RestController
@RequestMapping("/pagamento")
public interface PagamentoAPI {
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  PagamentoResponse processaPagamento(@Valid @RequestBody PagamentoRequest novoPagamento);

  @GetMapping("/{idPagamento}")
  PagamentoResponse buscaPagamentoPorId(@PathVariable UUID idPagamento);

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  PagamentoPageResponse buscaPagamentosPaginado(
      @RequestParam(value = "status", required = false) StatusPagamento statusPagamento,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size);

  @GetMapping("/pedido/{idPedido}")
  PagamentoPedidoResponse buscaPagamentoPorIdPedido(@PathVariable UUID idPedido);

  @PatchMapping("/cancelaPagamento/{idPagamento}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void cancelaPagamento(
      @PathVariable UUID idPagamento,
      @Valid @RequestBody CancelaPagamentoRequest cancelaPagamentoRequest);

  @PutMapping("/{idPagamento}/reprocessar")
  @ResponseStatus(HttpStatus.OK)
  ReprocessarPagamentoResponse reprocessaPagamento(@PathVariable UUID idPagamento);
}
