package br.com.wakax.wakax_ecommerce.produto.infra;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wakax.wakax_ecommerce.produto.domain.HistoricoAtualizacaoProduto;

public interface HistoricoAtualizacaoProdutoJPARepository
    extends JpaRepository<HistoricoAtualizacaoProduto, UUID> {}
