package br.com.wakax.wakax_ecommerce.produto.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wakax.wakax_ecommerce.produto.api.request.PrecoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoAtualizaResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListagemResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoResponse;
import br.com.wakax.wakax_ecommerce.produto.application.service.ProdutoService;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

  @Mock private ProdutoService produtoService;

  @InjectMocks private ProdutoController produtoController;

  private ProdutoRequest produtoRequest;
  private ProdutoResponse produtoResponse;
  private ProdutoListResponse produtoListResponse;
  private UUID produtoId;

  @BeforeEach
  void setUp() {
    produtoId = UUID.randomUUID();
    PrecoRequest precoRequest = new PrecoRequest();
    produtoRequest =
        ProdutoRequest.builder()
            .descricao("Produto Teste")
            .pesoLiquido(new BigDecimal("1.5"))
            .pesoBruto(new BigDecimal("2.0"))
            .descricaoComplementar("Descrição complementar")
            .preco(new BigDecimal("29.99"))
            .grupo("Eletrônicos")
            .unidade("UN")
            .estoqueMinimo(10)
            .estoqueMaximo(100)
            .precos(Collections.emptyList())
            .build();

    produtoResponse = mock(ProdutoResponse.class);
    produtoListResponse = mock(ProdutoListResponse.class);
  }

  @Test
  void deveCadastrarProdutoComSucesso() {
    when(produtoService.cadastraProduto(any(ProdutoRequest.class))).thenReturn(produtoResponse);
    ProdutoResponse response = produtoController.cadastraProduto(produtoRequest);
    assertNotNull(response);
    verify(produtoService, times(1)).cadastraProduto(produtoRequest);
  }

  @Test
  void deveBuscarProdutoPorIdComSucesso() {
    when(produtoService.buscaProdutoPorId(any(UUID.class))).thenReturn(produtoListResponse);
    ProdutoListResponse response = produtoController.buscaProdutoPorId(produtoId);
    assertNotNull(response);
    verify(produtoService, times(1)).buscaProdutoPorId(produtoId);
  }

  @Test
  void deveListarTodosProdutosComSucesso() {
    ProdutoListagemResponse mockResponse = mock(ProdutoListagemResponse.class);
    when(mockResponse.getTotalProdutos()).thenReturn(2L);
    when(mockResponse.getProdutos()).thenReturn(Collections.emptyList());
    when(produtoService.listarTodosProdutos(0, 20)).thenReturn(mockResponse);
    ProdutoListagemResponse response = produtoController.listarTodosProdutos(0, 20);
    assertNotNull(response);
    assertEquals(2L, response.getTotalProdutos());
    assertNotNull(response.getProdutos());
    verify(produtoService, times(1)).listarTodosProdutos(0, 20);
  }

  @Test
  void deveAtualizarProdutoComSucesso() {
    ProdutoAtualizaRequest atualizaRequest =
        new ProdutoAtualizaRequest(
            "Nova descrição", null, null, null, null, null, null, null, null);
    ProdutoAtualizaResponse atualizaResponse = mock(ProdutoAtualizaResponse.class);
    when(produtoService.atualizaProduto(produtoId, atualizaRequest)).thenReturn(atualizaResponse);

    ProdutoAtualizaResponse response =
        produtoController.atualizarProduto(produtoId, atualizaRequest);

    assertNotNull(response);
    verify(produtoService, times(1)).atualizaProduto(produtoId, atualizaRequest);
  }
}
