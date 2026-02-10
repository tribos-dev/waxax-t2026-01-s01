package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.util.UUID;

import javax.validation.Valid;

import br.com.wakax.wakax_ecommerce.auth.credencial.domain.Credencial;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.pedido.application.api.request.RastreamentoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;

@RestController
@RequestMapping("/pedidos")
public interface RastreamentoPedidoAPI {

  @PostMapping("/{idPedido}/rastreamento")
  @ResponseStatus(HttpStatus.CREATED)
  RastreamentoResponse cadastraRastreamento(
      @PathVariable UUID idPedido, @Valid @RequestBody RastreamentoRequest request);

  @GetMapping("/{idPedido}/rastreamento")
  @ResponseStatus(HttpStatus.OK)
  RastreamentoResponse consultaRastreamento(
      @AuthenticationPrincipal Credencial credencial,
      @PathVariable UUID idPedido);
}
