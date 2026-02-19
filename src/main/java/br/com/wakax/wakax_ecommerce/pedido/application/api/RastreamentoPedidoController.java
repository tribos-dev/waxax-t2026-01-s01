package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.auth.security.service.TokenService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.RastreamentoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.RastreamentoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.service.RastreamentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class RastreamentoPedidoController implements RastreamentoPedidoAPI {
  private final RastreamentoService rastreamentoService;
  private final TokenService tokenService;

  @Override
  public RastreamentoResponse cadastraRastreamento(UUID idPedido, RastreamentoRequest request) {
    log.debug("[start] RastreamentoPedidoController - cadastraRastreamento");
    RastreamentoResponse response = rastreamentoService.cadastraRastreamento(idPedido, request);
    log.debug("[finish] RastreamentoPedidoController - cadastraRastreamento");
    return response;
  }

  @Override
  public RastreamentoResponse consultaRastreamento(String token, UUID idPedido) {
    log.debug("[start] RastreamentoPedidoController - consultaRastreamento");
    String clientePorEmail = getUsuarioByToken(token);
    RastreamentoResponse rastreamentoResponse =
        rastreamentoService.consultaRastreamento(clientePorEmail, idPedido);
    log.debug("[finish] RastreamentoPedidoController - consultaRastreamento");
    return rastreamentoResponse;
  }

  private String getUsuarioByToken(String token) {
    log.info("[start] RastreamentoPedidoController - getUsuarioByToken");
    String usuario =
        tokenService
            .getUsuarioByBearerToken(token)
            .orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
    log.info("[finish] RastreamentoPedidoController - getUsuarioByToken");
    return usuario;
  }
}
