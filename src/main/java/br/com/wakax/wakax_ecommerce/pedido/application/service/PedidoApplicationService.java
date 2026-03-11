package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoListResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoPageResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoApplicationService implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final CarrinhoRepository carrinhoRepository;
  private final ClienteService clienteService;

  @Override
  @Transactional
  public PedidoResponse cadastraPedido(PedidoRequest request) {
    log.info("[start] PedidoApplicationService - cadastraPedido");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(request.getIdCarrinho());
    Cliente cliente = carrinho.getCliente();
    if(cliente.getPessoa().getStatus() == StatusPessoa.INATIVO){
      throw APIException.build(HttpStatus.CONFLICT,
              "Cliente está inativo e não pode realizar pedidos");
    }
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
  public PedidoPageResponse buscaPedidosDoCliente(
      UUID idCliente, StatusPedido statusPedido, int page, int size) {
    log.debug("[start] PedidoApplicationService - buscaPedidosDoCliente");
    clienteService.buscaClienteEspecifico(idCliente);
    Pageable pageable = PageRequest.of(page, size, Sort.by("dataPedido").descending());
    Page<Pedido> pedidosPaginados =
        pedidoRepository.buscaPedidosDoClientePaginado(idCliente, statusPedido, pageable);
    List<PedidoListResponse> pedidoList =
        pedidosPaginados.getContent().stream().map(PedidoListResponse::new).toList();
    log.debug("[finish] PedidoApplicationService - buscaPedidosDoCliente");
    return new PedidoPageResponse(idCliente, pedidoList, pedidosPaginados.getTotalPages());
  }
}
