package br.com.wakax.wakax_ecommerce.produto.infra;

import org.springframework.stereotype.Repository;

import br.com.wakax.wakax_ecommerce.produto.application.repository.HistoricoAtualizacaoProdutoRepository;
import br.com.wakax.wakax_ecommerce.produto.domain.HistoricoAtualizacaoProduto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@RequiredArgsConstructor
@Log4j2
public class HistoricoAtualizacaoProdutoInfraRepository
    implements HistoricoAtualizacaoProdutoRepository {

  private final HistoricoAtualizacaoProdutoJPARepository historicoJPARepository;

  @Override
  public HistoricoAtualizacaoProduto salva(HistoricoAtualizacaoProduto historico) {
    log.debug("[start] HistoricoAtualizacaoProdutoInfraRepository - salva");
    HistoricoAtualizacaoProduto historicoSalvo = historicoJPARepository.save(historico);
    log.debug("[finish] HistoricoAtualizacaoProdutoInfraRepository - salva");
    return historicoSalvo;
  }
}
