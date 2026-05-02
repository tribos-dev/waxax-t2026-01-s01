package br.com.wakax.wakax_ecommerce.cliente.application.service;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteBuscaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteAtualizaResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteResponse;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;

public interface ClienteService {
  ClienteResponse criaCliente(ClienteRequest clienteRequest);

  ClienteResponse buscaClienteEspecifico(UUID idCliente);

  Page<Cliente> buscarTodosOsClientes(Pageable pageable);

  ClienteResponse desativaCliente(UUID idCliente);

  ClienteAtualizaResponse atualizarCliente(UUID idCliente, ClienteAtualizaRequest clienteRequest);

  ClienteResponse ativarCliente(UUID idCliente);

  ClienteResponse inativarCliente(UUID idCliente);

  Page<Cliente> buscarClientePorCriterios(ClienteBuscaRequest filtro);
}
