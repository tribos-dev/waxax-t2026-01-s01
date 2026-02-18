package br.com.wakax.wakax_ecommerce.cliente.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.wakax.wakax_ecommerce.cliente.application.api.ClienteController;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteListAllResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.PageResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

  @Mock private ClienteService clienteService;

  @InjectMocks private ClienteController clienteController;

  @Test
  void deveListarClientesComSucesso() {
    PageRequest pageable = PageRequest.of(0, 10);
    Pessoa pessoa = mock(Pessoa.class);
    when(pessoa.getNome()).thenReturn("João");
    when(pessoa.getEmails()).thenReturn(List.of("joao@email.com"));
    when(pessoa.getStatus()).thenReturn(StatusPessoa.ATIVO);
    Cliente cliente = mock(Cliente.class);
    when(cliente.getPessoa()).thenReturn(pessoa);
    when(cliente.getDataCriacao()).thenReturn(LocalDateTime.now());
    when(cliente.getDataEdicao()).thenReturn(LocalDateTime.now());
    Page<Cliente> page = new PageImpl<>(List.of(cliente), pageable, 1);
    when(clienteService.buscarTodosOsClientes(pageable)).thenReturn(page);
    PageResponse<ClienteListAllResponse> response = clienteController.buscarTodosOsClientes(0, 10);
    assertNotNull(response);
    assertEquals(1, response.getTotalDeUsuarios());
    assertEquals(1, response.getContent().size());
    verify(clienteService).buscarTodosOsClientes(pageable);
  }

  @Test
  void deveRetornarListaVaziaQuandoNaoExistirClientes() {
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Cliente> pageVazia = new PageImpl<>(List.of(), pageable, 0);
    when(clienteService.buscarTodosOsClientes(pageable)).thenReturn(pageVazia);
    PageResponse<ClienteListAllResponse> response = clienteController.buscarTodosOsClientes(0, 10);
    assertNotNull(response);
    assertTrue(response.getContent().isEmpty());
    assertEquals(0, response.getTotalDeUsuarios());
    verify(clienteService, times(1)).buscarTodosOsClientes(pageable);
  }
}
