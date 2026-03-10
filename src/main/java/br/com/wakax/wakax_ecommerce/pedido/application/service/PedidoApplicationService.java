package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueService;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.StatusPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.PedidoStatusEvent;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoApplicationService implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final CarrinhoRepository carrinhoRepository;
  private final EstoqueService estoqueService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public PedidoResponse cadastraPedido(PedidoRequest request) {
    log.info("[start] PedidoApplicationService - cadastraPedido");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(request.getIdCarrinho());
    Pedido pedido = new Pedido(request, carrinho);
    pedidoRepository.salva(pedido);
    log.debug("[finish] PedidoApplicationService - cadastraPedido");
    return new PedidoResponse(pedido);
  }

  @Override
  public PedidoResponse buscaPedidoPorId(UUID idPedido) {
    log.debug("[start] PedidoApplicationService - buscaPedidoPorId");
    var pedido = pedidoRepository.buscaPedidoPorId(idPedido);
    log.debug("[finish] PedidoApplicationService - buscaPedidoPorId");
    return new PedidoResponse(pedido);
  }

  @Override
  @Transactional
  public void atualizaStatusPedido(UUID idPedido, StatusPedidoRequest statusPedidoRequest) {
    log.info("[start] PedidoApplicationService - atualizaStatusPedido");
    Pedido pedido = pedidoRepository.buscaPedidoPorId(idPedido);
    StatusPedido novoStatus = statusPedidoRequest.getNovoStatus();
    pedido.atualizarStatus(novoStatus);
    processaAcoesDeStatus(pedido, novoStatus);
    pedidoRepository.salva(pedido);
    log.info("[finish] PedidoApplicationService - atualizaStatusPedido");
  }

  private void processaAcoesDeStatus(Pedido pedido, StatusPedido novoStatus) {
    if (novoStatus == StatusPedido.CANCELADO) {
      liberaReservaDeProdutoNoEstoque(pedido);
    }

    if (novoStatus == StatusPedido.ENVIADO) {
      notificaCliente(pedido);
    }

    eventPublisher.publishEvent(new PedidoStatusEvent(pedido, novoStatus));
  }

  private void liberaReservaDeProdutoNoEstoque(Pedido pedido) {
    log.info("[estoque] Iniciando liberação de estoque para o pedido: {}", pedido.getId());
    pedido
        .getItensPedido()
        .forEach(
            item -> {
              estoqueService.liberaReserva(item.getProduto().getId(), item.getQuantidade());
              log.info(
                  "[estoque] Liberado: {} unidades para o produto: {}",
                  item.getQuantidade(),
                  item.getProduto().getDescricao());
            });
  }

  private void notificaCliente(Pedido pedido) {
    log.info("[envio] Notificando cliente sobre o envio: {}", pedido.getCliente().getId());
  }
}
