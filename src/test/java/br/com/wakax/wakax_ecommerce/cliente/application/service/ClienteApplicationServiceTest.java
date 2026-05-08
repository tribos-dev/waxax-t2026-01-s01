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
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.carrinho.application.service.CarrinhoService;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteBuscaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteAtualizaResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.repository.ClienteRepository;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;

@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

  @Mock private ClienteRepository clienteRepository;
  @Mock private CarrinhoService carrinhoService;

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
  void deveDesativarClienteQuandoEstiverAtivo() {
    UUID idCliente = UUID.randomUUID();
    Pessoa pessoa = new Pessoa();
    pessoa.setStatus(StatusPessoa.ATIVO);
    Cliente cliente =
        Cliente.builder()
            .id(idCliente)
            .pessoa(pessoa)
            .status(br.com.wakax.wakax_ecommerce.cliente.domain.StatusCliente.ATIVO)
            .dataCriacao(LocalDateTime.now())
            .dataEdicao(LocalDateTime.now())
            .build();

    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(cliente);

    clienteApplicationService.desativaCliente(idCliente);

    assertEquals(StatusPessoa.INATIVO, pessoa.getStatus());
    verify(clienteRepository, times(1)).salva(cliente);
  }

  @Test
  void naoDeveDesativarClienteQuandoJaEstiverInativo() {
    UUID idCliente = UUID.randomUUID();
    Pessoa pessoa = new Pessoa();
    pessoa.setStatus(StatusPessoa.INATIVO);
    Cliente cliente =
        Cliente.builder()
            .id(idCliente)
            .pessoa(pessoa)
            .status(br.com.wakax.wakax_ecommerce.cliente.domain.StatusCliente.INATIVO)
            .dataCriacao(LocalDateTime.now())
            .dataEdicao(LocalDateTime.now())
            .build();

    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(cliente);

    assertThrows(Exception.class, () -> clienteApplicationService.desativaCliente(idCliente));

    verify(clienteRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoAoAtivarClienteInexistente() {
    UUID idInexistente = UUID.randomUUID();
    when(clienteRepository.buscaClientePorId(idInexistente))
        .thenThrow(new APIException(HttpStatus.NOT_FOUND, ErrorCode.CLIENTE_NAO_ENCONTRADO));
    APIException exception =
        assertThrows(
            APIException.class, () -> clienteApplicationService.ativarCliente(idInexistente));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    verify(clienteRepository, times(1)).buscaClientePorId(idInexistente);
    verify(clienteRepository, never()).salva(any());
    verify(carrinhoService, never()).restaurarCarrinho(any());
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

  @Test
  void deveLancarExcecaoAoAtivarClienteJaAtivo() {
    UUID idCliente = UUID.randomUUID();
    Cliente cliente = mock(Cliente.class);
    when(clienteRepository.buscaClientePorId(idCliente)).thenReturn(cliente);
    when(cliente.isAtivo()).thenReturn(true);
    APIException exception =
        assertThrows(APIException.class, () -> clienteApplicationService.ativarCliente(idCliente));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    verify(cliente, never()).ativar();
    verify(clienteRepository, never()).salva(any());
    verify(carrinhoService, never()).restaurarCarrinho(any());
  }

  @Test
  void deveBuscarClientePorCpfExato() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();
    filtro.setCpf("026.801.105-28");

    Cliente clienteMock = mock(Cliente.class);
    Page<Cliente> pageMock = new PageImpl<>(List.of(clienteMock), PageRequest.of(0, 10), 1);

    when(clienteRepository.buscarClientePorCriterios(
            eq("026.801.105-28"), isNull(), isNull(), any()))
        .thenReturn(pageMock);

    Page<Cliente> resultado = clienteApplicationService.buscarClientePorCriterios(filtro);

    assertNotNull(resultado);
    assertEquals(1, resultado.getTotalElements());
    verify(clienteRepository, times(1))
        .buscarClientePorCriterios(eq("026.801.105-28"), isNull(), isNull(), any());
  }

  @Test
  void deveBuscarClientePorEmail() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();
    filtro.setEmail("ARIIRAFA8@EMAIL.COM");

    Cliente clienteMock = mock(Cliente.class);
    Page<Cliente> pageMock = new PageImpl<>(List.of(clienteMock), PageRequest.of(0, 10), 1);

    when(clienteRepository.buscarClientePorCriterios(
            isNull(), eq("ARIIRAFA8@EMAIL.COM"), isNull(), any()))
        .thenReturn(pageMock);

    Page<Cliente> resultado = clienteApplicationService.buscarClientePorCriterios(filtro);

    assertNotNull(resultado);
    assertEquals(1, resultado.getTotalElements());
    verify(clienteRepository, times(1))
        .buscarClientePorCriterios(isNull(), eq("ARIIRAFA8@EMAIL.COM"), isNull(), any());
  }

  @Test
  void deveBuscarClientePorNomeParcial() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();
    filtro.setNome("Aria");

    Cliente clienteMock = mock(Cliente.class);
    Page<Cliente> pageMock = new PageImpl<>(List.of(clienteMock), PageRequest.of(0, 10), 1);

    when(clienteRepository.buscarClientePorCriterios(isNull(), isNull(), eq("Aria"), any()))
        .thenReturn(pageMock);

    Page<Cliente> resultado = clienteApplicationService.buscarClientePorCriterios(filtro);

    assertNotNull(resultado);
    assertEquals(1, resultado.getTotalElements());
    verify(clienteRepository, times(1))
        .buscarClientePorCriterios(isNull(), isNull(), eq("Aria"), any());
  }

  @Test
  void deveBuscarClienteComMultiplosCriterios() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();
    filtro.setNome("Ariane");
    filtro.setEmail("ariirafa8@email.com");

    Cliente clienteMock = mock(Cliente.class);
    Page<Cliente> pageMock = new PageImpl<>(List.of(clienteMock), PageRequest.of(0, 10), 1);

    when(clienteRepository.buscarClientePorCriterios(
            isNull(), eq("ariirafa8@email.com"), eq("Ariane"), any()))
        .thenReturn(pageMock);

    Page<Cliente> resultado = clienteApplicationService.buscarClientePorCriterios(filtro);

    assertNotNull(resultado);
    assertEquals(1, resultado.getTotalElements());
    verify(clienteRepository, times(1))
        .buscarClientePorCriterios(isNull(), eq("ariirafa8@email.com"), eq("Ariane"), any());
  }

  @Test
  void deveLancarExcecaoQuandoNenhumCriterioForInformado() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();

    APIException exception =
        assertThrows(
            APIException.class, () -> clienteApplicationService.buscarClientePorCriterios(filtro));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    verify(clienteRepository, never()).buscarClientePorCriterios(any(), any(), any(), any());
  }

  @Test
  void deveRetornarListaVaziaQuandoNenhumClienteForEncontrado() {
    ClienteBuscaRequest filtro = new ClienteBuscaRequest();
    filtro.setEmail("naoexiste@email.com");

    Page<Cliente> pageVazia = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

    when(clienteRepository.buscarClientePorCriterios(
            isNull(), eq("naoexiste@email.com"), isNull(), any()))
        .thenReturn(pageVazia);

    Page<Cliente> resultado = clienteApplicationService.buscarClientePorCriterios(filtro);

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    assertEquals(0, resultado.getTotalElements());
    verify(clienteRepository, times(1))
        .buscarClientePorCriterios(isNull(), eq("naoexiste@email.com"), isNull(), any());
  }
}
