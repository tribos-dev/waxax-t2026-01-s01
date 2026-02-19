package br.com.wakax.wakax_ecommerce.carrinho.api.response;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.carrinho.domain.ItemCarrinho;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoResponse;
import lombok.Getter;

@Getter
public class ItemCarrinhoResponse {

  private UUID id;
  private ProdutoResponse produto;
  private Integer quantidade;
  private BigDecimal precoUnitario;
  private BigDecimal valorTotal;

  public ItemCarrinhoResponse(ItemCarrinho itemCarrinho) {
    this.id = itemCarrinho.getId();
    this.produto = new ProdutoResponse(itemCarrinho.getProduto());
    this.quantidade = itemCarrinho.getQuantidade();
    this.precoUnitario = itemCarrinho.getPrecoUnitario();
    this.valorTotal = itemCarrinho.getValorTotalDoItem();
  }
}
