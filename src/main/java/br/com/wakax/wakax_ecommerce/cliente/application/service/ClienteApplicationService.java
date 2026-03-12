package br.com.wakax.wakax_ecommerce.cliente.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteAtualizaResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ClienteApplicationService implements ClienteService {
  private final ClienteRepository clienteRepository;

  @Override
  public ClienteResponse criaCliente(ClienteRequest clienteRequest) {
    log.info("[start] ClienteApplicationService - criaCliente");
    Cliente clienteCriado = clienteRepository.salva(new Cliente(clienteRequest));
    log.debug("[finish] ClienteApplicationService - criaCliente");
    return new ClienteResponse(clienteCriado);
  }

  @Override
  public ClienteResponse buscaClienteEspecifico(UUID idCliente) {
    log.info("[start] ClienteApplicationService - buscaClienteEspecifico");
    Cliente cliente = clienteRepository.buscaClientePorId(idCliente);
    log.debug("[finish] ClienteApplicationService - buscaClienteEspecifico");
    return new ClienteResponse(cliente);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Cliente> buscarTodosOsClientes(Pageable pageable) {
    log.info("[start] ClienteApplicationService - buscarTodosOsClientes");
    Page<Cliente> clientes = clienteRepository.buscaTodosOsClientes(pageable);
    log.debug("[finish] ClienteApplicationService - buscarTodosOsClientes");
    return clientes;
  }

  @Override
  public ClienteResponse desativaCliente(UUID idCliente) {
    log.info("[start] ClienteApplicationService - desativaCliente");
    Cliente cliente = clienteRepository.buscaClientePorId(idCliente);
    cliente.desativar(cliente.getPessoa());
    clienteRepository.salva(cliente);
    log.info("[finish] ClienteApplicationService - desativaCliente");
    return new ClienteResponse(cliente);
  }

  @Transactional
  public ClienteAtualizaResponse atualizarCliente(
      UUID idCliente, ClienteAtualizaRequest clienteRequest) {
    log.debug("[start] ClienteApplicationService - atualizarCliente");
    Cliente cliente = clienteRepository.buscaClientePorId(idCliente);
    cliente.alterar(clienteRequest);
    clienteRepository.salva(cliente);
    log.debug("[finish] ClienteApplicationService - atualizarCliente");
    return new ClienteAtualizaResponse(cliente);
  }
}
