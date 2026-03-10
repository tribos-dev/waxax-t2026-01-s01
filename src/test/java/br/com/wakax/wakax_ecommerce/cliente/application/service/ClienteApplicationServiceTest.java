package br.com.wakax.wakax_ecommerce.cliente.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteAtualizaResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;

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

  @Test
  void deveAtualizarClienteComSucesso() {

    UUID idCliente = UUID.randomUUID();
    Cliente clienteMock = mock(Cliente.class);
    Pessoa pessoaMock = mock(Pessoa.class);
    ClienteAtualizaRequest request = mock(ClienteAtualizaRequest.class);

    Endereco enderecoFake = Endereco.builder().logradouro("Rua das Flores").numero("123").build();
    List<Endereco> listaEnderecos = new ArrayList<>(List.of(enderecoFake));
    List<String> emails = List.of("teste@email.com");
    List<String> telefones = List.of("11999999999");

    when(clienteMock.getPessoa()).thenReturn(pessoaMock);
    when(pessoaMock.getNome()).thenReturn("Rodrigo Dev");
    when(pessoaMock.getEmails()).thenReturn(emails);
    when(pessoaMock.getTelefones()).thenReturn(telefones);
    when(pessoaMock.getEnderecos()).thenReturn(listaEnderecos);
    when(clienteMock.getDataEdicao()).thenReturn(LocalDateTime.now());

    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(clienteMock);

    ClienteAtualizaResponse response =
        clienteApplicationService.atualizarCliente(idCliente, request);

    assertNotNull(response);
    assertNotNull(response.getEnderecos());
    assertEquals(1, response.getEnderecos().size());
    assertEquals("Rodrigo Dev", response.getNome());
    assertEquals(emails, response.getEmails());
    assertEquals(telefones, response.getTelefones());

    assertEquals("Rua das Flores", response.getEnderecos().get(0).getLogradouro());
    assertEquals("123", response.getEnderecos().get(0).getNumero());

    verify(clienteRepository, times(1)).salva(clienteMock);
  }
}
