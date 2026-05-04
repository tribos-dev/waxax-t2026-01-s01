package br.com.wakax.wakax_ecommerce.estoque.api.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdicionaQuantidadeRequest {
  @NotNull @Positive private Integer quantidade;
  @NotNull @PositiveOrZero private BigDecimal custoUnitario;
}
