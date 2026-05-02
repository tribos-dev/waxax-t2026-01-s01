package br.com.wakax.wakax_ecommerce.cliente.application.api;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteBuscaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ClienteController implements ClienteApi {

  private final ClienteService clienteService;

  @Override
  public ClienteResponse cadastrarCliente(ClienteRequest clienteRequest) {
    log.info("[start] ClienteController - cadastrarCliente");
    ClienteResponse clienteCriado = clienteService.criaCliente(clienteRequest);
    log.debug("[finish] ClienteController - cadastrarCliente");
    return clienteCriado;
  }

  @Override
  public ClienteResponse buscaClienteEspecifico(UUID idCliente) {
    log.info("[start] ClienteController - buscaClienteEspecifico");
    ClienteResponse cliente = clienteService.buscaClienteEspecifico(idCliente);
    log.debug("[finish] ClienteController - buscaClienteEspecifico");
    return cliente;
  }

  @Override
  public PageResponse<ClienteListAllResponse> buscarTodosOsClientes(int page, int size) {
    log.info("[start] ClienteController - buscarTodosOsClientes");
    Page<Cliente> clientes = clienteService.buscarTodosOsClientes(PageRequest.of(page, size));
    Page<ClienteListAllResponse> response = clientes.map(ClienteListAllResponse::new);
    log.debug("[finish] ClienteController - buscarTodosOsClientes");
    return PageResponse.from(response);
  }

  @Override
  public ClienteResponse desativaCliente(UUID idCliente) {
    log.info("[start] ClienteController - desativaCliente");
    ClienteResponse clienteResponse = clienteService.desativaCliente(idCliente);
    log.info("[finish] ClienteController - desativaCliente");
    return clienteResponse;
  }

  public ClienteAtualizaResponse atualizarCliente(
      UUID idCliente, ClienteAtualizaRequest clienteRequest) {
    log.debug("[start] ClienteController - atualizarCliente");
    ClienteAtualizaResponse response = clienteService.atualizarCliente(idCliente, clienteRequest);
    log.debug("[finish] ClienteController - atualizarCliente");
    return response;
  }

  @Override
  public ClienteResponse ativarCliente(UUID idCliente) {
    log.info("[start] ClienteController - ativarCliente - idCliente: {}", idCliente);
    ClienteResponse response = clienteService.ativarCliente(idCliente);
    log.info("[finish] ClienteController - ativarCliente");
    return response;
  }

  @Override
  public ClienteResponse inativarCliente(UUID idCliente) {
    log.info("[start] ClienteController - inativarCliente - idCliente: {}", idCliente);
    ClienteResponse response = clienteService.inativarCliente(idCliente);
    log.info("[finish] ClienteController - inativarCliente");
    return response;
  }

  @Override
  public PageResponse<ClienteListResponse> buscarClientePorCriterios(ClienteBuscaRequest filtro) {
    log.info("[start] ClienteController - buscarClientePorCriterio");
    Page<Cliente> clientes = clienteService.buscarClientePorCriterios(filtro);
    Page<ClienteListResponse> response = clientes.map(ClienteListResponse::new);
    log.debug("[finish] ClienteController - buscarClientePorCriterio");
    return PageResponse.from(response);
  }
}
