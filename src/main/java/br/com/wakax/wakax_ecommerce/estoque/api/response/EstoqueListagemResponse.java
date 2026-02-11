package br.com.wakax.wakax_ecommerce.estoque.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
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

}
