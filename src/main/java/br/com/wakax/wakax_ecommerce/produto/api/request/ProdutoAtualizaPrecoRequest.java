package br.com.wakax.wakax_ecommerce.produto.api.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import br.com.wakax.wakax_ecommerce.produto.domain.TipoPreco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoAtualizaPrecoRequest {

  @NotNull private TipoPreco tipoPreco;

  @NotNull @Positive private BigDecimal novoPreco;

  @Size(max = 255)
  @NotBlank(message = "Motivo é obrigatório")
  private String motivo;
}
