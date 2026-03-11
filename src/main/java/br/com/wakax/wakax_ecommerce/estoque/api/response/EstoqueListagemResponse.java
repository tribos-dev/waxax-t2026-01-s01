package br.com.wakax.wakax_ecommerce.estoque.api.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueListagemResponse {

  @JsonProperty("itens")
  private List<EstoqueResponse> itens;

  @JsonProperty("valorTotalInventario")
  private BigDecimal valorTotalInventario;

  @JsonProperty("totalItens")
  private Integer totalItens;

  public static EstoqueListagemResponse of(List<Estoque> estoques) {
    if (estoques == null || estoques.isEmpty()) {
      return EstoqueListagemResponse.builder()
          .itens(List.of())
          .valorTotalInventario(BigDecimal.ZERO)
          .totalItens(0)
          .build();
    }

    List<EstoqueResponse> itens = estoques.stream().map(EstoqueResponse::new).toList();

    BigDecimal valorTotal =
        estoques.stream().map(Estoque::getCustoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

    return EstoqueListagemResponse.builder()
        .itens(itens)
        .valorTotalInventario(valorTotal)
        .totalItens(itens.size())
        .build();
  }
}
