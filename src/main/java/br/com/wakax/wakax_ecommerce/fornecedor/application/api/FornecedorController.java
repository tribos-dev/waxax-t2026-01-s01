package br.com.wakax.wakax_ecommerce.fornecedor.application.api;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorPageResponse;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorListResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.service.FornecedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class FornecedorController implements FornecedorAPI {

  private final FornecedorService fornecedorService;

  @Override
  public FornecedorResponse cadastraFornecedor(FornecedorRequest novoFornecedor) {
    log.debug("[start] FornecedorController - cadastraFornecedor");
    FornecedorResponse response = fornecedorService.cadastraFornecedor(novoFornecedor);
    log.debug("[finish] FornecedorController - cadastraFornecedor");
    return response;
  }

  @Override
  public FornecedorListResponse buscaFornecedorPorId(UUID idFornecedor) {
    log.debug("[start] FornecedorController - buscaFornecedorPorId");
    FornecedorListResponse response = fornecedorService.buscaFornecedorPorId(idFornecedor);
    log.debug("[finish] FornecedorController - buscaFornecedorPorId");
    return response;
  }

  @Override
  public FornecedorPageResponse listaFornecedores(StatusPessoa status, int page, String sortBy, String sortDirection, int size) {
    log.debug("[start] FornecedorController - listaFornecedores");
    FornecedorPageResponse response = fornecedorService.listaFornecedores(status, page, sortBy, sortDirection, size);
    log.debug("[finish] FornecedorController - listaFornecedores");
    return response;
  }
}
