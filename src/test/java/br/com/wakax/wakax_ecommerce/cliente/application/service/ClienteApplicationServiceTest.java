package br.com.wakax.wakax_ecommerce.cliente.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;

@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

  @Mock private ClienteRepository clienteRepository;

  @InjectMocks private ClienteApplicationService clienteApplicationService;

  @Test
  void deveBuscarTodosOsClientesComPaginacao() {
    Pageable pageable = PageRequest.of(0, 10);
    Cliente cliente = mock(Cliente.class);
    Page<Cliente> page = new PageImpl<>(List.of(cliente), pageable, 1);
    when(clienteRepository.buscaTodosOsClientes(pageable)).thenReturn(page);
    Page<Cliente> response = clienteApplicationService.buscarTodosOsClientes(pageable);
    assertNotNull(response);
    assertEquals(1, response.getTotalElements());
    assertEquals(1, response.getContent().size());
    verify(clienteRepository, times(1)).buscaTodosOsClientes(pageable);
  }

  @Test
  void deveRetornarPaginaVaziaQuandoNaoExistiremClientes() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Cliente> pageVazia = new PageImpl<>(List.of(), pageable, 0);
    when(clienteRepository.buscaTodosOsClientes(pageable)).thenReturn(pageVazia);
    Page<Cliente> response = clienteApplicationService.buscarTodosOsClientes(pageable);
    assertNotNull(response);
    assertTrue(response.isEmpty());
    assertEquals(0, response.getTotalElements());
    verify(clienteRepository, times(1)).buscaTodosOsClientes(pageable);
  }
}
