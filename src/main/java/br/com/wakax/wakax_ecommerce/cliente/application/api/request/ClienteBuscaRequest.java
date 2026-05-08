package br.com.wakax.wakax_ecommerce.cliente.application.api.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClienteBuscaRequest {
  private String cpf;
  private String email;
  private String nome;
  private int page = 0;
  private int size = 10;
}
