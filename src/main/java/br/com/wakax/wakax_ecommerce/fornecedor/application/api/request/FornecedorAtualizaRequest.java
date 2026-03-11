package br.com.wakax.wakax_ecommerce.fornecedor.application.api.request;

import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FornecedorAtualizaRequest {

    @NotNull(message = "{validacao.emails.obrigatorio}")
    @NotEmpty(message = "{validacao.emails.obrigatorio}")
    private List<@NotBlank(message = "{validacao.email.obrigatorio}")
            @Size(max = 150, message = "{validacao.email.tamanho}")
            @javax.validation.constraints.Email(message = "{validacao.email.formato}") String> emails;

    @NotNull(message = "{validacao.emails.obrigatorio}")
    @NotEmpty(message = "{validacao.telefone.obrigatorio}")
    private List<@NotBlank(message = "{validacao.telefone.obrigatorio}")
            @NotEmpty
            @Size(max = 20, message = "{validacao.telefone.tamanho}")
            @javax.validation.constraints.Pattern(
                    regexp = "\\d{10,11}",
                    message = "{validacao.telefone.formato}") String> telefones;

    @NotNull(message = "{validacao.enderecos.obrigatorio}")
    @NotEmpty(message = "{validacao.enderecos.obrigatorio}")
    private List<Endereco> enderecos;


    @Size(max = 20, message = "{validacao.inscricaoestadual.tamanho}")
    private String inscricaoEstadual;

    @NotBlank(message = "{validacao.razaosocial.obrigatorio}")
    @Size(max = 100, message = "{validacao.razaosocial.tamanho}")
    private String razaoSocial;

    @NotBlank(message = "{validacao.razaosocial.obrigatorio}")
    @Size(max = 100, message = "{validacao.nomefantasia.tamanho}")
    private String nomeFantasia;
}
