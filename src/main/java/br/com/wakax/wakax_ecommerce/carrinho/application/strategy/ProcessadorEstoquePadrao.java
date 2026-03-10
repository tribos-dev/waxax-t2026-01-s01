package br.com.wakax.wakax_ecommerce.carrinho.application.strategy;

import org.springframework.stereotype.Component;

import br.com.wakax.wakax_ecommerce.estoque.api.request.RemoveEstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.application.service.EstoqueService;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ProcessadorEstoquePadrao implements ProcessadorEstoque {

  private final EstoqueService estoqueService;

  public ProcessadorEstoquePadrao(EstoqueService estoqueService) {
    this.estoqueService = estoqueService;
  }

  @Override
  public void aoAdicionarItem(Produto produto, Integer quantidade) {
    log.info("Iniciando baixa automática para o produto: {}", produto.getDescricao());

    RemoveEstoqueRequest request = new RemoveEstoqueRequest(quantidade);
    estoqueService.removeQuantidadeEstoque(produto.getId(), request);
    log.debug("Iniciando baixa automática para o produto: {}", produto.getDescricao());
  }

  @Override
  public void aoAlterarQuantidade(
      Produto produto, Integer quantidadeAtual, Integer novaQuantidade) {}

  @Override
  public void aoRemoverItem(Produto produto, Integer quantidade) {}
}
