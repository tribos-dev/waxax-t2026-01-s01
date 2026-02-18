package br.com.wakax.wakax_ecommerce.carrinho.api.response;

import br.com.wakax.wakax_ecommerce.carrinho.domain.Carrinho;
import br.com.wakax.wakax_ecommerce.carrinho.domain.StatusCarrinho;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CarrinhosListAllResponse {

    private UUID idCarrinho;
    private LocalDateTime dataCriacao;
    private StatusCarrinho statusCarrinho;

    public CarrinhosListAllResponse(Carrinho carrinho) {
        this.idCarrinho = carrinho.getId();
        this.dataCriacao = carrinho.getDataCriacao();
        this.statusCarrinho = carrinho.getStatusCarrinho();
    }

}
