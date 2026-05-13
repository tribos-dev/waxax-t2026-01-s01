package br.com.wakax.wakax_ecommerce.produto.api.response;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.TipoPreco;
import lombok.Getter;

@Getter
public class ProdutoAtualizaPrecoResponse {
  private final UUID idProduto;
  private final TipoPreco tipoPreco;
  private final BigDecimal novoPreco;

  public ProdutoAtualizaPrecoResponse(Preco preco) {
    this.idProduto = preco.getProduto().getId();
    this.tipoPreco = preco.getTipo();
    this.novoPreco = preco.getValor();
  }
}
