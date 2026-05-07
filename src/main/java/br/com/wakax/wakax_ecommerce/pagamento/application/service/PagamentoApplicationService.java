package br.com.wakax.wakax_ecommerce.pagamento.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueApplicationService;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.CancelaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.EstornaPagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.request.PagamentoRequest;
import br.com.wakax.wakax_ecommerce.pagamento.application.api.response.*;
import br.com.wakax.wakax_ecommerce.pagamento.application.factory.ProcessadorPagamentoFactory;
import br.com.wakax.wakax_ecommerce.pagamento.application.repository.PagamentoRepository;
import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import br.com.wakax.wakax_ecommerce.pagamento.domain.StatusPagamento;
import br.com.wakax.wakax_ecommerce.pagamento.infra.PagamentoInfraRepository;
import br.com.wakax.wakax_ecommerce.pedido.application.repository.PedidoRepository;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PagamentoApplicationService implements PagamentoService {

  private final PagamentoRepository pagamentoRepository;
  private final PedidoRepository pedidoRepository;
  private final ProcessadorPagamentoFactory processadorFactory;
  private final EstoqueRepository estoqueRepository;
  private PagamentoInfraRepository pagamentoInfraRepository;
  private final EstoqueApplicationService estoqueApplicationService;

  @Override
  @Transactional
  public PagamentoResponse processaPagamento(PagamentoRequest novoPagamento) {
    log.debug("[start] PagamentoApplicationService - criaPagamento");

    Pedido pedido = pedidoRepository.buscaPedidoPorId(novoPagamento.getPedidoId());

    Cliente cliente = pedido.getCliente();
    cliente.validaClienteAtivo();

    Pagamento pagamento = new Pagamento(pedido);

    var processador = processadorFactory.obterProcessador(pedido.getFormaPagamento());
    processador.processar(pagamento, pedido);

    pedidoRepository.salva(pedido);
    pagamentoRepository.salva(pagamento);
    log.debug("[finish] PagamentoApplicationService - criaPagamento");
    return new PagamentoResponse(pagamento);
  }

  private void verificarSeExistePagamento(UUID pedidoId) {
    pagamentoRepository
        .buscaPagamentoPorPedidoId(pedidoId)
        .ifPresent(
            pagamentoExistente -> {
              throw new APIException(
                  HttpStatus.CONFLICT,
                  ErrorCode.PEDIDO_JA_POSSUI_PAGAMENTO,
                  pagamentoExistente.getStatusPagamento());
            });
  }

  @Override
  public PagamentoResponse buscaPagamentoPorId(UUID idPagamento) {
    log.debug("[start] PagamentoApplicationService - buscaPagamentoPorId");
    Pagamento pagamento = pagamentoRepository.buscaPagamentoPorId(idPagamento);
    log.debug("[finish] PagamentoApplicationService - buscaPagamentoPorId");
    return new PagamentoResponse(pagamento);
  }

  @Override
  public PagamentoPageResponse buscaPagamentosPaginado(
      StatusPagamento statusPagamento, int page, int size) {
    log.debug("[start] PagamentoApplicationService - buscaPagamentosPaginado");
    Pageable pageable = PageRequest.of(page, size, Sort.by("dataPagamento").descending());
    Page<Pagamento> pagamentos =
        pagamentoRepository.buscaPagamentosPaginado(statusPagamento, pageable);
    log.debug("[finish] PagamentoApplicationService - buscaPagamentosPaginado");
    return PagamentoPageResponse.convertePaginado(
        pagamentos.getContent(), pagamentos.getTotalElements(), pagamentos.getTotalPages());
  }

  @Override
  public PagamentoPedidoResponse buscaPagamentoPorIdPedido(UUID idPedido) {
    log.debug("[start] PagamentoApplicationService - buscaPagamentoPorIdPedido");
    Pagamento pagamento =
        pagamentoRepository
            .buscaPagamentoPorPedidoId(idPedido)
            .orElseThrow(
                () ->
                    new APIException(HttpStatus.NOT_FOUND, ErrorCode.PEDIDO_NAO_POSSUI_PAGAMENTO));
    log.debug("[finish] PagamentoApplicationService - buscaPagamentoPorIdPedido");
    return new PagamentoPedidoResponse(pagamento);
  }

  @Override
  @Transactional
  public PagamentoResponse confirmarPagamento(UUID idPagamento) {
    log.debug("[start] PagamentoApplicationService - confirmarPagamento");
    Pagamento pagamento = pagamentoRepository.buscaPagamentoPorId(idPagamento);
    validarPagamentoAguardando(pagamento);
    pagamento.confirmarPagamento();
    Pedido pedido = pagamento.getPedido();
    pedido.marcarComoPago();
    reservarEstoqueDoPedido(pedido);
    pedidoRepository.salva(pedido);
    pagamentoRepository.salva(pagamento);
    log.debug("[finish] PagamentoApplicationService - confirmarPagamento");
    return new PagamentoResponse(pagamento);
  }

  private void validarPagamentoAguardando(Pagamento pagamento) {
    if (pagamento.getStatusPagamento() != StatusPagamento.AGUARDANDO) {
      throw new APIException(
          HttpStatus.CONFLICT, ErrorCode.PAGAMENTO_JA_CONFIRMADO, pagamento.getStatusPagamento());
    }
  }

  private void reservarEstoqueDoPedido(Pedido pedido) {
    pedido
        .getItensPedido()
        .forEach(
            item -> {
              Estoque estoque =
                  estoqueRepository
                      .buscaEstoquePorIdProduto(item.getProduto().getId())
                      .orElseThrow(
                          () ->
                              new APIException(
                                  HttpStatus.NOT_FOUND,
                                  ErrorCode.ESTOQUE_NAO_ENCONTRADO,
                                  item.getProduto().getId()));
              estoque.reservaQuantidade(item.getQuantidade());
              estoqueRepository.salva(estoque);
            });
  }

  @Override
  public void cancelaPagamento(UUID idPagamento, CancelaPagamentoRequest cancelaPagamentoRequest) {
    log.info("[start] PagamentoApplicationService - cancelaPagamento");
    Pagamento pagamento = pagamentoRepository.buscaPagamentoPorId(idPagamento);
    pagamento.mudaStatusParaFalhou(cancelaPagamentoRequest);
    pagamentoRepository.salva(pagamento);
    Pedido pedido = pagamento.getPedido();
    pedido.mudaStatusAguardandoPagamento();
    pedidoRepository.salva(pedido);
    pedidoRepository.salva(pedido);
    log.info("[finish] PagamentoApplicationService - cancelaPagamento");
  }

  @Override
  public ReprocessarPagamentoResponse reprocessaPagamento(UUID idPagamento) {
    log.info("[start] PagamentoApplicationService - reprocessaPagamento");

    Pagamento pagamento = pagamentoRepository.buscaPagamentoPorId(idPagamento);
    Pedido pedido = pagamento.getPedido();

    pagamento.prepararReprocessamento();

    var processador = processadorFactory.obterProcessador(pedido.getFormaPagamento());
    processador.processar(pagamento, pedido);

    pedidoRepository.salva(pedido);
    pagamentoRepository.salva(pagamento);
    log.debug("[finish] PagamentoApplicationService - reprocessaPagamento");
    return new ReprocessarPagamentoResponse(pagamento);
  }

  @Override
  public EstornarPagamentoResponse estornaPagamento(
      UUID idPagamento, EstornaPagamentoRequest estornaPagamentoRequest) {
    log.info("[start] PagamentoApplicationService - estornaPagamento");
    Pagamento pagamento = pagamentoRepository.buscaPagamentoPorId(idPagamento);

    pagamento.prepararEstorno(estornaPagamentoRequest);

    pagamentoRepository.salva(pagamento);

    log.debug("[finish] PagamentoApplicationService - estornaPagamento");
    return new EstornarPagamentoResponse(pagamento);
  }
}
