package br.com.wakax.wakax_ecommerce.pagamento.application.api.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@Getter
public class PagamentoPageResponse {
    private final Page<PagamentoResponse> pagamentos;
    private final long totalRegistros;
    private final BigDecimal valorTotal;
    private final long totalPago;

    public PagamentoPageResponse(Page<PagamentoResponse> pagamentos, long totalRegistros, BigDecimal valorTotal, long totalPago) {
        this.pagamentos = pagamentos;
        this.totalRegistros = totalRegistros;
        this.valorTotal = valorTotal;
        this.totalPago = totalPago;
    }
}
