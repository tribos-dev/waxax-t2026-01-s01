package br.com.wakax.wakax_ecommerce.produto.application.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.wakax.wakax_ecommerce.produto.domain.Produto;

public interface ProdutoRepository {
  Produto salva(Produto produto);

  Produto buscaProdutoPorId(UUID idProduto);

  
  Page<Produto> listarTodosProdutosPaginado(Pageable pageable);
}
