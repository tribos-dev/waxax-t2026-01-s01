package br.com.wakax.wakax_ecommerce.estoque.api.response;

import java.math.BigDecimal;
import java.util.List;

public class EstoqueListagemResponse {
    private BigDecimal valorTotalInventario;
    private List<EstoqueResponse> itens;
}
