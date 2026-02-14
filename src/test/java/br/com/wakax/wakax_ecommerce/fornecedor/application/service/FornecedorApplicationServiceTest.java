package br.com.wakax.wakax_ecommerce.fornecedor.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Sort;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorPageResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.repository.FornecedorRepository;
import br.com.wakax.wakax_ecommerce.fornecedor.domain.Fornecedor;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;

@ExtendWith(MockitoExtension.class)
public class FornecedorApplicationServiceTest {

  @Mock private FornecedorRepository fornecedorRepository;

  @InjectMocks private FornecedorApplicationService fornecedorApplicationService;

  @Test
  void deveListarTodosFornecedoresComSucesso() {

    Fornecedor fornecedor1 = FornecedorDataHelper.criarFornecedorAtivo();
    Fornecedor fornecedor2 = FornecedorDataHelper.criarFornecedorInativo();

    Page<Fornecedor> pageMock =
        new PageImpl<>(List.of(fornecedor1, fornecedor2), PageRequest.of(0, 20), 2);

    when(fornecedorRepository.buscaFornecedoresComFiltro(eq(null), any(Pageable.class)))
        .thenReturn(pageMock);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "razaoSocial"));
    FornecedorPageResponse response =
        fornecedorApplicationService.listaFornecedores(null, pageable);

    assertNotNull(response);
    assertEquals(2, response.getFornecedores().size());
    assertEquals(2, response.getTotalElements());
    assertEquals(1, response.getTotalPages());

    verify(fornecedorRepository).buscaFornecedoresComFiltro(eq(null), any(Pageable.class));

    verifyNoMoreInteractions(fornecedorRepository);
  }

  @Test
  void deveRetornarListaVaziaQuandoNaoExistemFornecedores() {

    Page<Fornecedor> pageVazia = Page.empty();

    when(fornecedorRepository.buscaFornecedoresComFiltro(eq(null), any(Pageable.class)))
        .thenReturn(pageVazia);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "razaoSocial"));
    FornecedorPageResponse response =
        fornecedorApplicationService.listaFornecedores(null, pageable);

    assertNotNull(response);
    assertTrue(response.getFornecedores().isEmpty());
    assertEquals(0, response.getTotalElements());

    verify(fornecedorRepository).buscaFornecedoresComFiltro(eq(null), any(Pageable.class));

    verifyNoMoreInteractions(fornecedorRepository);
  }

  @Test
  void deveFiltrarApenasFornecedoresAtivos() {

    Fornecedor fornecedor = FornecedorDataHelper.criarFornecedorAtivo();

    Page<Fornecedor> pageMock = new PageImpl<>(List.of(fornecedor));

    when(fornecedorRepository.buscaFornecedoresComFiltro(
            eq(StatusPessoa.ATIVO), any(Pageable.class)))
        .thenReturn(pageMock);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "razaoSocial"));
    FornecedorPageResponse response =
        fornecedorApplicationService.listaFornecedores(StatusPessoa.ATIVO, pageable);

    assertNotNull(response);
    assertEquals(1, response.getFornecedores().size());
    assertEquals(StatusPessoa.ATIVO, response.getFornecedores().get(0).getStatus());

    verify(fornecedorRepository)
        .buscaFornecedoresComFiltro(eq(StatusPessoa.ATIVO), any(Pageable.class));

    verifyNoMoreInteractions(fornecedorRepository);
  }

  @Test
  void deveFiltrarApenasFornecedoresInativos() {

    Fornecedor fornecedor = FornecedorDataHelper.criarFornecedorInativo();

    Page<Fornecedor> pageMock = new PageImpl<>(List.of(fornecedor));

    when(fornecedorRepository.buscaFornecedoresComFiltro(
            eq(StatusPessoa.INATIVO), any(Pageable.class)))
        .thenReturn(pageMock);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "razaoSocial"));
    FornecedorPageResponse response =
        fornecedorApplicationService.listaFornecedores(StatusPessoa.INATIVO, pageable);

    assertNotNull(response);
    assertEquals(1, response.getFornecedores().size());
    assertEquals(StatusPessoa.INATIVO, response.getFornecedores().get(0).getStatus());

    verify(fornecedorRepository)
        .buscaFornecedoresComFiltro(eq(StatusPessoa.INATIVO), any(Pageable.class));

    verifyNoMoreInteractions(fornecedorRepository);
  }

  @Test
  void deveRetornarFornecedoresComInformacoesCompletas() {

    Fornecedor fornecedor =
        FornecedorDataHelper.criarFornecedor(
            "Silva Comercio LTDA", "12.345.678/0001-90", StatusPessoa.ATIVO);

    Page<Fornecedor> pageMock = new PageImpl<>(List.of(fornecedor));

    when(fornecedorRepository.buscaFornecedoresComFiltro(eq(null), any(Pageable.class)))
        .thenReturn(pageMock);

    Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "razaoSocial"));
    FornecedorPageResponse response =
        fornecedorApplicationService.listaFornecedores(null, pageable);

    assertNotNull(response);
    assertEquals(1, response.getFornecedores().size());
    assertEquals("Nome Silva Comercio LTDA", response.getFornecedores().get(0).getNome());
    assertEquals("12.345.678/0001-90", response.getFornecedores().get(0).getDocumento());
    assertEquals("Silva Comercio LTDA", response.getFornecedores().get(0).getRazaoSocial());
    assertEquals(
        "Fantasia Silva Comercio LTDA", response.getFornecedores().get(0).getNomeFantasia());
    assertEquals(StatusPessoa.ATIVO, response.getFornecedores().get(0).getStatus());
    assertNotNull(response.getFornecedores().get(0).getDataCriacao());
    assertNotNull(response.getFornecedores().get(0).getDataEdicao());

    verify(fornecedorRepository).buscaFornecedoresComFiltro(eq(null), any(Pageable.class));

    verifyNoMoreInteractions(fornecedorRepository);
  }

  @Test
  void deveLancarExcecaoQuandoSortDirectionForInvalido() {

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Pageable pageable =
              PageRequest.of(0, 20, Sort.by(Sort.Direction.fromString("INVALIDO"), "razaoSocial"));
          fornecedorApplicationService.listaFornecedores(null, pageable);
        });

    verifyNoInteractions(fornecedorRepository);
  }
}
