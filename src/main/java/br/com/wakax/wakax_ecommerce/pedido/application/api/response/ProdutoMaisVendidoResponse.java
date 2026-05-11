package br.com.wakax.wakax_ecommerce.pedido.application.api.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoMaisVendidoResponse {
  private UUID produtoId;
  private String descricaoProduto;
  private Long quantidadeTotal;
  private BigDecimal receitaBruta;
}
