package br.com.wakax.wakax_ecommerce.cliente.application.api.response;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class ClienteListAllResponse {

    private String nome;
    private String email;
    private StatusPessoa statusPessoa;



    public ClienteListAllResponse(Cliente cliente) {
        this.nome = cliente.getPessoa().getNome();
        this.email =
                cliente.getPessoa().getEmails() != null
                        ? cliente.getPessoa().getEmails().stream().findFirst().orElse(null)
                        : null;
        this.statusPessoa = cliente.getPessoa().getStatus();

    }

}
