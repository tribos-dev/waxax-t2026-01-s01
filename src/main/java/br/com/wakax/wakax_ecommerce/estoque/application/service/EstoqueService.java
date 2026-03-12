package br.com.wakax.wakax_ecommerce.estoque.application.service;

import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.estoque.api.request.EstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.request.RemoveEstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;

public interface EstoqueService {
  EstoqueResponse criaEstoque(UUID idProduto, EstoqueRequest request);

  EstoqueResponse buscaEstoquePorIdProduto(UUID idProduto);

  boolean temQuantidadeDisponivel(UUID idProduto, Integer quantidade);

  void reservaQuantidade(UUID idProduto, Integer quantidade);

  void liberaReserva(UUID idProduto, Integer quantidade);

  void liberaReservaDePedido(List<ItemPedido> itensPedido);

  EstoqueListagemResponse listarTodoEstoque(Integer quantidadeMinima, Boolean apenasEmFalta);

  void removeQuantidadeEstoque(UUID idProduto, RemoveEstoqueRequest request);
}
