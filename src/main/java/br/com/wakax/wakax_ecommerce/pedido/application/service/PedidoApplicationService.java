package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.wakax.wakax_ecommerce.carrinho.application.repository.CarrinhoRepository;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.EstornaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.repository.PagamentoRepository;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoListResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.PedidoPageResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.CancelamentoPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.EnderecoEntregaRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.StatusPedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoApplicationService implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final CarrinhoRepository carrinhoRepository;
  private final ClienteService clienteService;
  private final EstoqueService estoqueService;
  private final PagamentoRepository pagamentoRepository;

  @Override
  @Transactional
  public PedidoResponse cadastraPedido(PedidoRequest request) {
    log.info("[start] PedidoApplicationService - cadastraPedido");
    Carrinho carrinho = carrinhoRepository.buscaCarrinhoPorId(request.getIdCarrinho());
    Cliente cliente = carrinho.getCliente();
    cliente.validaSeClienteEstaAtivo();
    Pedido pedido = new Pedido(request, carrinho);
    pedidoRepository.salva(pedido);
    carrinho.finalizar();
    carrinhoRepository.salva(carrinho);
    log.info("[pedido] Pedido criado e carrinho finalizado: {}", carrinho.getId());
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

  @Override
  @Transactional
  public void atualizaStatusPedido(UUID idPedido, StatusPedidoRequest statusPedidoRequest) {
    log.debug("[start] PedidoApplicationService - atualizaStatusPedido");
    Pedido pedido = pedidoRepository.buscaPedidoPorId(idPedido);
    StatusPedido statusAnterior = pedido.getStatus();
    StatusPedido novoStatus = statusPedidoRequest.getNovoStatus();
    pedido.atualizarStatus(novoStatus);
    processaAcoesDeStatus(pedido, statusAnterior, novoStatus);
    pedidoRepository.salva(pedido);
    log.debug("[finish] PedidoApplicationService - atualizaStatusPedido");
  }

  @Override
  @Transactional
  public void cancelarPedido(UUID idPedido, CancelamentoPedidoRequest cancelamentoPedidoRequest) {
    log.debug("[start] PedidoApplicationService - cancelarPedido");
    Pedido pedido = pedidoRepository.buscaPedidoPorId(idPedido);
    pedido.cancelar(cancelamentoPedidoRequest.getMotivoCancelamento());
    processaEstornoDePagamentoQuandoAplicavel(pedido, cancelamentoPedidoRequest);
    liberaReservaDeProdutoNoEstoque(pedido);
    pedidoRepository.salva(pedido);
    log.debug("[finish] PedidoApplicationService - cancelarPedido");
  }

  private void processaEstornoDePagamentoQuandoAplicavel(
      Pedido pedido, CancelamentoPedidoRequest cancelamentoPedidoRequest) {
    pagamentoRepository
        .buscaPagamentoPorPedidoId(pedido.getId())
        .filter(pagamento -> pagamento.getStatusPagamento() == StatusPagamento.PAGO)
        .ifPresent(
            pagamento -> {
              pagamento.prepararEstorno(
                  new EstornaPagamentoRequest(cancelamentoPedidoRequest.getMotivoCancelamento()));
              pagamentoRepository.salva(pagamento);
            });
  }

  private void processaAcoesDeStatus(
      Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus) {
    if (novoStatus == StatusPedido.CANCELADO && statusAnterior != StatusPedido.CANCELADO) {
      liberaReservaDeProdutoNoEstoque(pedido);
    }
  }

  private void liberaReservaDeProdutoNoEstoque(Pedido pedido) {
    log.debug("[estoque] Iniciando liberação de estoque para o pedido: {}", pedido.getId());
    estoqueService.liberaReservaDePedido(pedido.getItensPedido());
  }

  @Override
  public List<ProdutoMaisVendidoResponse> geraRelatorioProdutosMaisVendidos(
      LocalDateTime dataInicio, LocalDateTime dataFim, Integer limite) {
    log.debug("[start] PedidoApplicationService - geraRelatorioProdutosMaisVendidos");
    validarDatas(dataInicio, dataFim);
    Integer limiteNormalizado = normalizarLimite(limite);
    Pageable pageable = PageRequest.of(0, limiteNormalizado);
    List<ProdutoMaisVendidoResponse> produtos =
        pedidoRepository.buscaProdutosMaisVendidos(dataInicio, dataFim, pageable);
    log.debug("[finish] PedidoApplicationService - geraRelatorioProdutosMaisVendidos");
    return produtos;
  }

  private void validarDatas(LocalDateTime dataInicio, LocalDateTime dataFim) {
    if (dataInicio == null || dataFim == null) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.RELATORIO_DATA_OBRIGATORIA);
    }
    if (dataInicio.isAfter(dataFim)) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.RELATORIO_DATA_INVALIDA);
    }
  }

  private int normalizarLimite(Integer limite) {
    if (limite == null || limite <= 0) return 10;
    if (limite > 100) return 100;
    return limite;
  }

  @Override
  @Transactional
  public void alteraEnderecoEntrega(UUID idPedido, EnderecoEntregaRequest request) {
    log.debug("[start] PedidoApplicationService - alteraEnderecoEntrega");
    Pedido pedido = pedidoRepository.buscaPedidoPorId(idPedido);
    Cliente cliente = pedido.getCliente();
    Endereco novoEndereco = cliente.buscaEnderecoEspecifico(request.getIdEnderecoEntrega());
    pedido.alteraEnderecoDeEntrega(novoEndereco);
    pedidoRepository.salva(pedido);
    log.debug("[finish] PedidoApplicationService - alteraEnderecoEntrega");
  }
}
