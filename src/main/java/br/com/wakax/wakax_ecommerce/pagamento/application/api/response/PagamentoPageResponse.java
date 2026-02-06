package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import br.com.wakax.wakax_ecommerce.pagamento.domain.Pagamento;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagamentoPageResponse {
    private final List<PagamentoResponse> pagamentos;
    private final long totalPagamentos;

    public static PagamentoPageResponse convertePaginado(List<Pagamento> pagamentos, long totalElements) {
        List<PagamentoResponse> dtos = pagamentos.stream()
                .map(PagamentoResponse::new)
                .collect(Collectors.toList());

        return new PagamentoPageResponse(dtos, totalElements);
    }
}
