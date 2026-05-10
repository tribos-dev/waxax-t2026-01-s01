package br.com.wakax.wakax_ecommerce.produto.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.auth.usuario.domain.Usuario;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaPrecoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoAtualizaPrecoResponse;
import br.com.wakax.wakax_ecommerce.produto.application.repository.HistoricoAtualizacaoProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.application.repository.ProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.HistoricoAtualizacaoProduto;
import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import br.com.wakax.wakax_ecommerce.produto.domain.TipoPreco;

@ExtendWith(MockitoExtension.class)
class ProdutoApplicationServiceTest {

  @Mock private ProdutoRepository produtoRepository;

  @Mock private HistoricoAtualizacaoProdutoRepository historicoAtualizacaoProdutoRepository;

  @InjectMocks private ProdutoApplicationService produtoApplicationService;

  private ProdutoRequest produtoRequest;
  private UUID produtoId;

  @BeforeEach
  void setUp() {
    produtoId = UUID.randomUUID();

    produtoRequest =
        ProdutoRequest.builder()
            .descricao("Produto Teste")
            .pesoLiquido(new BigDecimal("1.5"))
            .pesoBruto(new BigDecimal("2.0"))
            .descricaoComplementar("Descrição complementar do produto")
            .preco(new BigDecimal("29.99"))
            .grupo("Eletrônicos")
            .unidade("UN")
            .estoqueMinimo(10)
            .estoqueMaximo(100)
            .precos(Collections.emptyList())
            .build();
  }

  private void mockProdutoRepositorySalvaComId() {
    when(produtoRepository.salva(any(Produto.class)))
        .thenAnswer(
            (Answer<Produto>)
                invocation -> {
                  Produto p = invocation.getArgument(0);
                  p.setId(produtoId);
                  return p;
                });
  }

  private Produto criaProdutoComPrecoPadrao(BigDecimal valor) {
    Produto produto = new Produto(produtoRequest);
    produto.setId(produtoId);

    Preco precoPadrao = new Preco(TipoPreco.PADRAO, valor, produto);

    produto.setPrecos(new ArrayList<>());
    produto.getPrecos().add(precoPadrao);

    return produto;
  }

  @Test
  void deveAtualizarPrecoComSucesso() {
    Produto produto = criaProdutoComPrecoPadrao(new BigDecimal("50.00"));

    Usuario usuarioLogado = mock(Usuario.class);

    when(produtoRepository.buscaProdutoPorId(produtoId)).thenReturn(produto);

    ProdutoApplicationService spyService = Mockito.spy(produtoApplicationService);

    doReturn(usuarioLogado).when(spyService).buscaUsuarioLogado();

    ProdutoAtualizaPrecoRequest request =
        ProdutoAtualizaPrecoRequest.builder()
            .tipoPreco(TipoPreco.PADRAO)
            .novoPreco(new BigDecimal("99.99"))
            .motivo("Ajuste de preço")
            .build();

    ProdutoAtualizaPrecoResponse response = spyService.atualizaPreco(produtoId, request);

    assertNotNull(response);
    assertEquals(produtoId, response.getIdProduto());
    assertEquals(TipoPreco.PADRAO, response.getTipoPreco());
    assertEquals(new BigDecimal("99.99"), response.getNovoPreco());

    assertEquals(new BigDecimal("99.99"), produto.buscaPrecoPorTipo(TipoPreco.PADRAO).getValor());

    verify(produtoRepository, times(1)).buscaProdutoPorId(produtoId);

    verify(produtoRepository, times(1)).salva(produto);

    verify(historicoAtualizacaoProdutoRepository, times(1))
        .salva(any(HistoricoAtualizacaoProduto.class));
  }

  @Test
  void deveLancarExcecaoQuandoProdutoNaoEncontradoParaAtualizarPreco() {
    when(produtoRepository.buscaProdutoPorId(produtoId))
        .thenThrow(
            new APIException(HttpStatus.NOT_FOUND, ErrorCode.PRODUTO_NAO_ENCONTRADO, produtoId));

    ProdutoAtualizaPrecoRequest request =
        ProdutoAtualizaPrecoRequest.builder()
            .tipoPreco(TipoPreco.PADRAO)
            .novoPreco(new BigDecimal("99.99"))
            .build();

    APIException exception =
        assertThrows(
            APIException.class, () -> produtoApplicationService.atualizaPreco(produtoId, request));

    assertEquals(ErrorCode.PRODUTO_NAO_ENCONTRADO, exception.getErrorCode());

    verify(produtoRepository, times(1)).buscaProdutoPorId(produtoId);

    verify(produtoRepository, never()).salva(any());

    verify(historicoAtualizacaoProdutoRepository, never()).salva(any());
  }

  @Test
  void deveLancarExcecaoQuandoPrecoNaoEncontrado() {
    Produto produto = new Produto(produtoRequest);
    produto.setId(produtoId);
    produto.setPrecos(new ArrayList<>());

    when(produtoRepository.buscaProdutoPorId(produtoId)).thenReturn(produto);

    ProdutoAtualizaPrecoRequest request =
        ProdutoAtualizaPrecoRequest.builder()
            .tipoPreco(TipoPreco.PROMOCIONAL)
            .novoPreco(new BigDecimal("99.99"))
            .build();

    APIException exception =
        assertThrows(
            APIException.class, () -> produtoApplicationService.atualizaPreco(produtoId, request));

    assertEquals(ErrorCode.PRECO_NAO_ENCONTRADO, exception.getErrorCode());

    verify(produtoRepository, times(1)).buscaProdutoPorId(produtoId);

    verify(produtoRepository, never()).salva(any());

    verify(historicoAtualizacaoProdutoRepository, never()).salva(any());
  }

  @Test
  void deveSalvarHistoricoAoAtualizarPreco() {
    Produto produto = criaProdutoComPrecoPadrao(new BigDecimal("50.00"));

    Usuario usuarioLogado = mock(Usuario.class);

    when(produtoRepository.buscaProdutoPorId(produtoId)).thenReturn(produto);

    ProdutoApplicationService spyService = Mockito.spy(produtoApplicationService);

    doReturn(usuarioLogado).when(spyService).buscaUsuarioLogado();

    ProdutoAtualizaPrecoRequest request =
        ProdutoAtualizaPrecoRequest.builder()
            .tipoPreco(TipoPreco.PADRAO)
            .novoPreco(new BigDecimal("150.00"))
            .motivo("Ajuste sazonal")
            .build();

    spyService.atualizaPreco(produtoId, request);

    ArgumentCaptor<HistoricoAtualizacaoProduto> historicoCaptor =
        ArgumentCaptor.forClass(HistoricoAtualizacaoProduto.class);

    verify(historicoAtualizacaoProdutoRepository, times(1)).salva(historicoCaptor.capture());

    HistoricoAtualizacaoProduto historicoSalvo = historicoCaptor.getValue();

    assertEquals(produto, historicoSalvo.getProduto());
    assertEquals(usuarioLogado, historicoSalvo.getUsuario());

    assertEquals(TipoPreco.PADRAO, historicoSalvo.getTipoPreco());

    assertEquals(new BigDecimal("50.00"), historicoSalvo.getValorAnterior());

    assertEquals(new BigDecimal("150.00"), historicoSalvo.getValorNovo());

    assertEquals("Ajuste sazonal", historicoSalvo.getMotivo());

    assertNotNull(historicoSalvo.getDataHora());
  }

  @Test
  void deveAtualizarPrecoSemMotivo() {
    Produto produto = criaProdutoComPrecoPadrao(new BigDecimal("50.00"));

    Usuario usuarioLogado = mock(Usuario.class);

    when(produtoRepository.buscaProdutoPorId(produtoId)).thenReturn(produto);

    ProdutoApplicationService spyService = Mockito.spy(produtoApplicationService);

    doReturn(usuarioLogado).when(spyService).buscaUsuarioLogado();

    ProdutoAtualizaPrecoRequest request =
        ProdutoAtualizaPrecoRequest.builder()
            .tipoPreco(TipoPreco.PADRAO)
            .novoPreco(new BigDecimal("75.50"))
            .build();

    ProdutoAtualizaPrecoResponse response = spyService.atualizaPreco(produtoId, request);

    assertNotNull(response);

    assertEquals(new BigDecimal("75.50"), produto.buscaPrecoPorTipo(TipoPreco.PADRAO).getValor());

    ArgumentCaptor<HistoricoAtualizacaoProduto> historicoCaptor =
        ArgumentCaptor.forClass(HistoricoAtualizacaoProduto.class);

    verify(historicoAtualizacaoProdutoRepository, times(1)).salva(historicoCaptor.capture());

    HistoricoAtualizacaoProduto historicoSalvo = historicoCaptor.getValue();

    assertNull(historicoSalvo.getMotivo());

    verify(produtoRepository, times(1)).buscaProdutoPorId(produtoId);

    verify(produtoRepository, times(1)).salva(produto);
  }
}
