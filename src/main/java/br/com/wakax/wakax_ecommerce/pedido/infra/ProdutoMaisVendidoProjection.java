package br.com.wakax.wakax_ecommerce.pedido.infra;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProdutoMaisVendidoProjection {
  UUID getProdutoId();
  String getDescricaoProduto();
  Long getQuantidadeTotal();
  BigDecimal getReceitaBruta();
}
