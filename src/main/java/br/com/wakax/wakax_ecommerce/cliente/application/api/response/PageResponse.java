package br.com.wakax.wakax_ecommerce.cliente.application.api.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int paginaAtual;
    private long totalDeUsuarios;
    private int totalDePaginas;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}