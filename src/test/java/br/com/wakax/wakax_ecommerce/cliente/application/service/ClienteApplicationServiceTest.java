package br.com.wakax.wakax_ecommerce.cliente.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.carrinho.application.service.CarrinhoService;
import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteResponse;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

  @Mock private ClienteRepository clienteRepository;
  @Mock
  private CarrinhoService carrinhoService;

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

  @Test
  void deveAtivarClienteInativo() {
    UUID idCliente = UUID.randomUUID();
    Cliente cliente = mock(Cliente.class);
    Pessoa pessoa = mock(Pessoa.class);
    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(cliente);
    when(cliente.isAtivo()).thenReturn(false);
    when(clienteRepository.salva(cliente)).thenReturn(cliente);
    when(cliente.getPessoa()).thenReturn(pessoa);
    when(pessoa.getStatus()).thenReturn(StatusPessoa.ATIVO);
    when(cliente.getId()).thenReturn(idCliente);
    ClienteResponse response = clienteApplicationService.ativarCliente(idCliente);
    verify(cliente).ativar();
    verify(clienteRepository).salva(cliente);
    verify(carrinhoService).restaurarCarrinho(idCliente);
    assertNotNull(response);

  }

  @Test
  void deveLancarExcecaoAoAtivarClienteInexistente() {
    UUID idInexistente = UUID.randomUUID();
    when(clienteRepository.buscaClientePorId(idInexistente)).thenThrow(new APIException(HttpStatus.NOT_FOUND, ErrorCode.CLIENTE_NAO_ENCONTRADO));
    APIException exception = assertThrows(APIException.class, () -> clienteApplicationService.ativarCliente(idInexistente));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    verify(clienteRepository, times(1)).buscaClientePorId(idInexistente);
    verify(clienteRepository, never()).salva(any());
    verify(carrinhoService, never()).restaurarCarrinho(any());
  }

  @Test
  void deveLancarExcecaoAoAtivarClienteJaAtivo() {
    UUID idCliente = UUID.randomUUID();
    Cliente cliente = mock(Cliente.class);
    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(cliente);
    when(cliente.isAtivo()).thenReturn(true);
    APIException exception = assertThrows(APIException.class, () -> clienteApplicationService.ativarCliente(idCliente));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    verify(cliente, never()).ativar();
    verify(clienteRepository, never()).salva(any());
    verify(carrinhoService, never()).restaurarCarrinho(any());

  }
}

