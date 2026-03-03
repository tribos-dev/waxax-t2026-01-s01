package br.com.wakax.wakax_ecommerce.pessoa.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorUpdateRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorUpdateResponse;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorListResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.repository.FornecedorRepository;
import br.com.wakax.wakax_ecommerce.fornecedor.application.service.FornecedorApplicationService;
import br.com.wakax.wakax_ecommerce.fornecedor.domain.Fornecedor;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class FornecedorApplicationServiceTest {

  @Mock private FornecedorRepository fornecedorRepository;

  @InjectMocks private FornecedorApplicationService fornecedorApplicationService;

  private FornecedorRequest fornecedorRequest;
  private Fornecedor fornecedor;
  private UUID fornecedorId;
  private FornecedorUpdateRequest fornecedorUpdateRequest;

  private Pessoa criarPessoaPadrao() {
    return Pessoa.builder()
            .emails(List.of("email@antigo.com"))
            .telefones(List.of("11911111111"))
            .enderecos(List.of())
            .build();
  }

  private Fornecedor criarFornecedorExistente(UUID id) {
    return Fornecedor.builder()
            .id(id)
            .pessoa(criarPessoaPadrao())
            .documento("12345678000199")
            .inscricaoEstadual("12345")
            .razaoSocial("Razão Antiga")
            .nomeFantasia("Fantasia Antiga")
            .dataCriacao(LocalDateTime.now())
            .dataEdicao(LocalDateTime.now())
            .build();
  }

  private FornecedorUpdateRequest criarRequestAtualizacao() {
    return new FornecedorUpdateRequest(
            List.of("novo@email.com"),
            List.of("11999999999"),
            List.of(),
            "99999",
            "Nova Razão Social",
            "Novo Nome Fantasia"
    );
  }

  @BeforeEach
  void setUp() {
    fornecedorId = UUID.randomUUID();

    List<String> emails = Arrays.asList("contato@empresa.com.br", "vendas@empresa.com.br");
    List<String> telefones = Arrays.asList("(11) 99999-9999", "(11) 88888-8888");
    List<Endereco> enderecos =
        Arrays.asList(
            Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("123")
                .complemento("Sala 45")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado("SP")
                .cep("01234-567")
                .principal(true)
                .build());

    fornecedorRequest =
        new FornecedorRequest(
            "Empresa ABC Ltda",
            "12.345.678/0001-90",
            emails,
            telefones,
            enderecos,
            "123456789",
            "Empresa ABC Ltda",
            "ABC");

  }



  private void mockFornecedorRepositorySalvaComId() {
    when(fornecedorRepository.salva(any(Fornecedor.class)))
        .thenAnswer(
            (Answer<Fornecedor>)
                invocation -> {
                  Fornecedor f = invocation.getArgument(0);
                  f.setId(fornecedorId);
                  return f;
                });
  }
  private void mockFornecedorRepositoryAtualizar() {
    when(fornecedorRepository.atualiza(any(Fornecedor.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void deveCadastrarFornecedorComSucesso() {
    mockFornecedorRepositorySalvaComId();
    FornecedorResponse response =
        fornecedorApplicationService.cadastraFornecedor(fornecedorRequest);
    assertNotNull(response);
    assertEquals(fornecedorId, response.getId());
    assertEquals(fornecedorRequest.getDocumento(), response.getDocumento());
    verify(fornecedorRepository, times(1)).salva(any(Fornecedor.class));
  }

  @Test
  void deveLancarExcecaoQuandoFornecedorDuplicado() {
    when(fornecedorRepository.salva(any(Fornecedor.class)))
        .thenThrow(
            new APIException(
                org.springframework.http.HttpStatus.CONFLICT,
                ErrorCode.FORNECEDOR_DUPLICADO,
                fornecedorRequest.getDocumento()));

    APIException exception =
        assertThrows(
            APIException.class,
            () -> {
              fornecedorApplicationService.cadastraFornecedor(fornecedorRequest);
            });

    assertEquals(ErrorCode.FORNECEDOR_DUPLICADO, exception.getErrorCode());
    assertEquals(fornecedorRequest.getDocumento(), exception.getArgs()[0]);
    verify(fornecedorRepository, times(1)).salva(any(Fornecedor.class));
  }

  @Test
  void deveBuscarFornecedorPorIdComSucesso() {
    Fornecedor fornecedor = new Fornecedor(fornecedorRequest);
    fornecedor.setId(fornecedorId);
    when(fornecedorRepository.buscaFornecedorPorId(fornecedorId)).thenReturn(fornecedor);

    FornecedorListResponse response =
        fornecedorApplicationService.buscaFornecedorPorId(fornecedorId);

    assertNotNull(response);
    assertEquals(fornecedorId, response.getId());
    assertEquals(fornecedorRequest.getNome(), response.getNome());
    assertEquals(fornecedorRequest.getDocumento(), response.getDocumento());
    assertEquals(fornecedorRequest.getInscricaoEstadual(), response.getInscricaoEstadual());
    assertEquals(fornecedorRequest.getRazaoSocial(), response.getRazaoSocial());
    assertEquals(fornecedorRequest.getNomeFantasia(), response.getNomeFantasia());

    verify(fornecedorRepository, times(1)).buscaFornecedorPorId(fornecedorId);
  }

  @Test
  void deveLancarExcecaoQuandoFornecedorNaoEncontrado() {
    when(fornecedorRepository.buscaFornecedorPorId(fornecedorId))
        .thenThrow(
            new APIException(
                org.springframework.http.HttpStatus.NOT_FOUND,
                ErrorCode.FORNECEDOR_NAO_ENCONTRADO,
                fornecedorId));

    APIException exception =
        assertThrows(
            APIException.class,
            () -> {
              fornecedorApplicationService.buscaFornecedorPorId(fornecedorId);
            });

    assertEquals(ErrorCode.FORNECEDOR_NAO_ENCONTRADO, exception.getErrorCode());
    assertEquals(fornecedorId, exception.getArgs()[0]);
    verify(fornecedorRepository, times(1)).buscaFornecedorPorId(fornecedorId);
  }

  @Test
  void deveAtualizarFornecedorComSucesso() {

    UUID idFornecedor = UUID.randomUUID();
    Fornecedor fornecedor = criarFornecedorExistente(idFornecedor);
    FornecedorUpdateRequest request = criarRequestAtualizacao();

    when(fornecedorRepository.buscaFornecedorPorId(idFornecedor))
            .thenReturn(fornecedor);

    when(fornecedorRepository.atualiza(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

    FornecedorUpdateResponse response =
            fornecedorApplicationService.atualizarFornecedor(idFornecedor, request);

    assertNotNull(response);
    assertEquals("Nova Razão Social", response.getRazaoSocial());
    assertEquals("Novo Nome Fantasia", response.getNomeFantasia());
    assertEquals("99999", response.getInscricaoEstadual());

    verify(fornecedorRepository).buscaFornecedorPorId(idFornecedor);
    verify(fornecedorRepository).atualiza(any());
  }

  @Test
  void deveLancarExcecaoAoAtualizarQuandoFornecedorNaoEncontrado() {

    UUID idFornecedor = UUID.randomUUID();
    FornecedorUpdateRequest request = criarRequestAtualizacao();

    when(fornecedorRepository.buscaFornecedorPorId(idFornecedor))
            .thenThrow(new APIException(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.FORNECEDOR_NAO_ENCONTRADO,
                    idFornecedor
            ));

    APIException exception = assertThrows(
            APIException.class,
            () -> fornecedorApplicationService.atualizarFornecedor(idFornecedor, request)
    );

    assertEquals(ErrorCode.FORNECEDOR_NAO_ENCONTRADO, exception.getErrorCode());
    assertEquals(idFornecedor, exception.getArgs()[0]);

    verify(fornecedorRepository, times(1)).buscaFornecedorPorId(idFornecedor);
    verify(fornecedorRepository, never()).atualiza(any());
  }
}
