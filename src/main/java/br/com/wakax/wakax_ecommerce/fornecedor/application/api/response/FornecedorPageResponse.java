package br.com.wakax.wakax_ecommerce.fornecedor.application.api.response;

import java.util.List;

import org.springframework.data.domain.Page;

import br.com.wakax.wakax_ecommerce.fornecedor.domain.Fornecedor;
import lombok.Getter;

@Getter
public class FornecedorPageResponse {

  private List<FornecedorResumoResponse> fornecedores;
  private long totalElements;
  private int totalPages;
  private int currentPage;
  private int size;

  public FornecedorPageResponse(Page<Fornecedor> page) {
    this.fornecedores = page.getContent().stream().map(FornecedorResumoResponse::new).toList();

    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.currentPage = page.getNumber();
    this.size = page.getSize();
  }
}
