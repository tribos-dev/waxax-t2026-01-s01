package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagamentoPageResponse {
  private final List<PagamentoResponse> pagamentos;
  private final long totalPagamentos;
  private final long totalPaginas;
  private final BigDecimal valorTotalPagamentos;

  public static PagamentoPageResponse convertePaginado(List<Pagamento> pagamentos, long totalElements, int totalPages) {
    List<PagamentoResponse> dto =
        pagamentos.stream().map(PagamentoResponse::new).collect(Collectors.toList());

    BigDecimal valorTotalFiltro =
        dto.stream().map(PagamentoResponse::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    return new PagamentoPageResponse(dto, totalElements, totalPages, valorTotalFiltro);
  }
}
