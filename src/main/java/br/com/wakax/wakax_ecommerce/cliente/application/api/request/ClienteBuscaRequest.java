package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClienteBuscaRequest {
    private String cpf;
    private String email;
    private String nome;
    private int page;
    private int size;
}
