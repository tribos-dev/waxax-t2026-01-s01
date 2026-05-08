package br.com.wakax.wakax_ecommerce.produto.application.service;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoAtualizaResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListagemResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoResponse;

public interface ProdutoService {
  ProdutoResponse cadastraProduto(ProdutoRequest novoProduto);

  ProdutoListResponse buscaProdutoPorId(UUID idProduto);

  ProdutoListagemResponse listarTodosProdutos(int page, int size);

  ProdutoAtualizaResponse atualizaProduto(UUID idProduto, ProdutoAtualizaRequest atualizaRequest);

  void removerProduto(UUID idProduto);
}
