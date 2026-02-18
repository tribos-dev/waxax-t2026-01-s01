package br.com.wakax.wakax_ecommerce.estoque.infra;

import br.com.wakax.wakax_ecommerce.estoque.application.repository.EstoqueRepository;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Repository
@RequiredArgsConstructor
public class EstoqueInfraRepository implements EstoqueRepository {

    private final EstoqueJPARepository estoqueJPARepository;

    @Override
    public Estoque salva(Estoque estoque) {
        log.info("[start] EstoqueInfraRepository - salva");
        Estoque estoqueSalvo = estoqueJPARepository.save(estoque);
        log.debug("[finish] EstoqueInfraRepository - salva");
        return estoqueSalvo;
    }

    @Override
    public Optional<Estoque> buscaEstoquePorIdProduto(UUID idProduto) {
        log.info("[start] EstoqueInfraRepository - buscaEstoquePorIdProduto");
        Optional<Estoque> estoque = estoqueJPARepository.findByProdutoId(idProduto);
        log.debug("[finish] EstoqueInfraRepository - buscaEstoquePorIdProduto");
        return estoque;
    }

    @Override
    public Estoque buscaEstoquePorId(UUID idEstoque) {
        log.info("[start] EstoqueInfraRepository - buscaEstoquePorId");
        Estoque estoque =
                estoqueJPARepository
                        .findById(idEstoque)
                        .orElseThrow(
                                () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.ESTOQUE_NAO_ENCONTRADO));
        log.debug("[finish] EstoqueInfraRepository - buscaEstoquePorId");
        return estoque;
    }

    @Override
    public List<Estoque> buscaTodosEstoques() {
        return estoqueJPARepository.findAll();
    }
    @Override
    public List<Estoque> buscarComFiltro(Integer quantidadeMinima, Boolean apenasEmFalta) {
        if (Boolean.TRUE.equals(apenasEmFalta)) {
            return estoqueJPARepository.findByQuantidadeDisponivelEquals(0);
        }
        if (quantidadeMinima != null) {
            return estoqueJPARepository.findByQuantidadeDisponivelGreaterThanEqual(quantidadeMinima);
        }
        return estoqueJPARepository.findAll();
    }
}

