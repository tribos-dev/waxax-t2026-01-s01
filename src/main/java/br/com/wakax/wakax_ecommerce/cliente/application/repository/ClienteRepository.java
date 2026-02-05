package br.com.wakax.wakax_ecommerce.cliente.application.repository;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository {
  Cliente salva(Cliente cliente);

  Cliente buscaClientePorId(UUID idCliente);
  Page<Cliente> buscaTodosOsClientes(Pageable pageable);
}
