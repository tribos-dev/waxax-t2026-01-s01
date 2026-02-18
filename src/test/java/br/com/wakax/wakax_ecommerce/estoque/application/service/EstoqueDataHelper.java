package br.com.wakax.wakax_ecommerce.estoque.application.service;

import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import br.com.wakax.wakax_ecommerce.produto.domain.StatusProduto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class EstoqueDataHelper {

    private EstoqueDataHelper() {
    }

    public static Produto criarProdutoComPreco(String descricao, BigDecimal precoVenda) {
        Produto produto = Produto.builder()
                .descricao(descricao)
                .pesoLiquido(BigDecimal.ONE)
                .pesoBruto(BigDecimal.ONE)
                .status(StatusProduto.ATIVO)
                .build();

        Preco preco = Preco.builder()
                .valor(precoVenda)
                .produto(produto)
                .build();

        produto.setPrecos(List.of(preco));
        return produto;
    }

    public static List<Estoque> listaEstoquePadrao() {
        return List.of(
                criarEstoque("Mouse", 5, "10.00", "50.00"),
                criarEstoque("Notebook", 2, "20.00", "40.00"));
    }

    private static Estoque criarEstoque(String descricao, int qtd, String custoMedio, String custoTotal) {
        Produto produto = criarProdutoComPreco(descricao, new BigDecimal(custoMedio));
        return Estoque.builder()
                .produto(produto)
                .quantidadeDisponivel(qtd)
                .custoMedio(new BigDecimal(custoMedio))
                .custoTotal(new BigDecimal(custoTotal))
                .build();
    }

    public static EstoqueListagemResponse responsePadrao() {
        return EstoqueListagemResponse.builder()
                .itens(List.of(item("Mouse", 5), item("Notebook", 2)))
                .valorTotalInventario(new BigDecimal("90"))
                .build();
    }

    public static EstoqueListagemResponse responseVazia() {
        return EstoqueListagemResponse.builder().itens(Collections.emptyList())
                .valorTotalInventario(BigDecimal.ZERO)
                .build();
    }

    private static EstoqueResponse item(String descricao, int qtd) {
        return EstoqueResponse.builder().descricaoProduto(descricao)
                .quantidadeDisponivel(qtd)
                .build();
    }
}

