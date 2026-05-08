package br.com.wakax.wakax_ecommerce.produto.application.repository;

import br.com.wakax.wakax_ecommerce.produto.domain.HistoricoAtualizacaoProduto;

public interface HistoricoAtualizacaoProdutoRepository {
  HistoricoAtualizacaoProduto salva(HistoricoAtualizacaoProduto historico);
}
