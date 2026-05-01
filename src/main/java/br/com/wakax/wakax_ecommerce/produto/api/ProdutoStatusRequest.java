package br.com.wakax.wakax_ecommerce.produto.api;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
@Builder
public class ProdutoStatusRequest {

    @NotBlank
    private String status;
    private String motivo;

}
