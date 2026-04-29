package br.com.wakax.wakax_ecommerce.produto.api.request;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoAtualizaRequest {
  @Size(max = 150, message = "O número máximo de caracteres é 150")
  private String descricao;

  @Positive(message = "Peso líquido deve ser maior do que 0.")
  private BigDecimal pesoLiquido;

  @Positive(message = "Peso bruto deve ser maior do que 0.")
  private BigDecimal pesoBruto;

  @Size(max = 500, message = "O número máximo de caracteres é 500.")
  private String descricaoComplementar;

  @Valid private List<PrecoRequest> precos;

  @Size(max = 50, message = "O número máximo de caracteres é 50.")
  private String grupo;

  @Size(max = 10, message = "O número máximo de caracteres é 10.")
  private String unidade;

  private Integer estoqueMinimo;
  private Integer estoqueMaximo;
}
