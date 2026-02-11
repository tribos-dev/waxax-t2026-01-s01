package br.com.wakax.wakax_ecommerce.fornecedor.application.api.response;

import br.com.wakax.wakax_ecommerce.fornecedor.domain.Fornecedor;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FornecedorResumoResponse {

    private String nome;
    private String documento;
    private String razaoSocial;
    private String nomeFantasia;
    private StatusPessoa status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEdicao;

    public FornecedorResumoResponse(Fornecedor fornecedor) {
        this.nome = fornecedor.getPessoa().getNome();
        this.documento = fornecedor.getDocumento();
        this.razaoSocial = fornecedor.getRazaoSocial();
        this.nomeFantasia = fornecedor.getNomeFantasia();
        this.status = fornecedor.getPessoa().getStatus();
        this.dataCriacao = fornecedor.getDataCriacao();
        this.dataEdicao = fornecedor.getDataEdicao();
    }

}
