package br.com.wakax.wakax_ecommerce.fornecedor.application.api;

import java.util.UUID;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorAtualizaRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorFiltroRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorAtualizaResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorListResponse;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorPageResponse;
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
  public FornecedorPageResponse listaFornecedores(@ModelAttribute FornecedorFiltroRequest filtro) {
    log.debug("[start] FornecedorController - listaFornecedores");
    FornecedorPageResponse response =
        fornecedorService.listaFornecedores(filtro.getStatus(), filtro.toPageable());
    log.debug("[finish] FornecedorController - listaFornecedores");
    return response;
  }

  @Override
  public FornecedorAtualizaResponse atualizarFornecedor(
      UUID idFornecedor, FornecedorAtualizaRequest atualizaFornecedor) {
    log.debug("[start] FornecedorController - atualizarFornecedor");
    FornecedorAtualizaResponse response =
        fornecedorService.atualizarFornecedor(idFornecedor, atualizaFornecedor);
    log.debug("[finish] FornecedorController - atualizarFornecedor");
    return response;
  }

  @Override
  public void removerFornecedor(UUID idFornecedor) {
    log.debug("[start] FornecedorController - removerFornecedor");
    fornecedorService.removerFornecedor(idFornecedor);
    log.debug("[finish] FornecedorController - removerFornecedor");
  }
}
