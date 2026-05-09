package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.CancelamentoPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.StatusPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.service.PedidoService;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class PedidoController implements PedidoAPI {
  private final PedidoService pedidoService;

  @Override
  public PedidoResponse cadastraPedido(@Valid PedidoRequest pedidoRequest) {
    log.debug("[start] PedidoController - cadastraPedido");
    PedidoResponse response = pedidoService.cadastraPedido(pedidoRequest);
    log.debug("[finish] PedidoController - cadastraPedido");
    return response;
  }

  @Override
  public PedidoResponse buscaPedidoPorId(UUID idPedido) {
    log.debug("[start] PedidoController - buscaPedidoPorId");
    PedidoResponse response = pedidoService.buscaPedidoPorId(idPedido);
    log.debug("[finish] PedidoController - buscaPedidoPorId");
    return response;
  }

  @Override
  public PedidoPageResponse buscaPedidosDoCliente(
      UUID idCliente, StatusPedido statusPedido, int page, int size) {
    log.debug("[start] PedidoController - buscaPedidosDoCliente");
    PedidoPageResponse resonse =
        pedidoService.buscaPedidosDoCliente(idCliente, statusPedido, page, size);
    log.debug("[finish] PedidoController - buscaPedidosDoCliente");
    return resonse;
  }

  @Override
  public void atualizaStatus(UUID idPedido, StatusPedidoRequest statusPedidoRequest) {
    log.debug("[start] PedidoController - atualizaStatus");
    pedidoService.atualizaStatusPedido(idPedido, statusPedidoRequest);
    log.debug("[finish] PedidoController - atualizaStatus");
  }

  @Override
  public void cancelaPedido(UUID idPedido, CancelamentoPedidoRequest cancelamentoPedidoRequest) {
    log.debug("[start] PedidoController - cancelaPedido");
    pedidoService.cancelarPedido(idPedido, cancelamentoPedidoRequest);
    log.debug("[finish] PedidoController - cancelaPedido");
  }
}
