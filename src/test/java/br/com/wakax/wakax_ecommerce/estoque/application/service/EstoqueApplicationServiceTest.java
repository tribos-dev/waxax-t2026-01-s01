package br.com.wakax.wakax_ecommerce.estoque.application.service;

import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueApplicationServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueApplicationService estoqueService;

    @Test
    void deveListarTodoEstoqueComSucesso() {
        List<Estoque> estoques = EstoqueDataHelper.listaEstoquePadrao();
        when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(estoques);
        EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
        assertNotNull(response);
        assertEquals(2, response.getItens().size());
        assertEquals("Mouse", response.getItens().get(0).getDescricaoProduto());
        assertEquals("Notebook", response.getItens().get(1).getDescricaoProduto());
        verify(estoqueRepository, times(1)).buscarComFiltro(null, null);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaEstoque() {
        when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(Collections.emptyList());
        EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
        assertNotNull(response);
        assertTrue(response.getItens().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getValorTotalInventario()));
        verify(estoqueRepository).buscarComFiltro(null, null);
    }

    @Test
    void deveCalcularValorTotalDoInventario() {
        List<Estoque> estoques = EstoqueDataHelper.listaEstoquePadrao();
        when(estoqueRepository.buscarComFiltro(null, null)).thenReturn(estoques);
        EstoqueListagemResponse response = estoqueService.listarTodoEstoque(null, null);
        assertEquals(0, new BigDecimal("90").compareTo(response.getValorTotalInventario()));
        verify(estoqueRepository).buscarComFiltro(null, null);
    }
}