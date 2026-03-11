package br.com.wakax.wakax_ecommerce.fornecedor.application.api.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.fornecedor.domain.Fornecedor;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import lombok.Getter;

@Getter
public class FornecedorAtualizaResponse {

    private final UUID id;
    private final String razaoSocial;
    private final String nomeFornecedor;
    private final String nomeFantasia;
    private final String inscricaoEstadual;
    private final LocalDateTime dataAtualizacao;

    private final List<String> emails;
    private final List<String> telefones;
    private final List<Endereco> enderecos;

    public FornecedorAtualizaResponse(Fornecedor fornecedor) {
        this.id = fornecedor.getId();
        this.razaoSocial = fornecedor.getRazaoSocial();
        this.nomeFornecedor = fornecedor.getPessoa().getNome();
        this.nomeFantasia = fornecedor.getNomeFantasia();
        this.inscricaoEstadual = fornecedor.getInscricaoEstadual();
        this.dataAtualizacao = fornecedor.getDataEdicao();
        this.emails = fornecedor.getPessoa().getEmails();
        this.telefones = fornecedor.getPessoa().getTelefones();
        this.enderecos = fornecedor.getPessoa().getEnderecos();

    }
}