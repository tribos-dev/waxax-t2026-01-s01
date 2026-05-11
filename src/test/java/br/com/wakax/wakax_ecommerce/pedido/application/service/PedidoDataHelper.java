package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pedido.application.api.request.EnderecoEntregaRequest;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;

public final class PedidoDataHelper {

  private PedidoDataHelper() {}

  public static Pedido criaPedidoResumo(
      LocalDateTime dataPedido, StatusPedido statusPedido, BigDecimal valorTotal) {
    return Pedido.builder()
        .id(UUID.randomUUID())
        .dataPedido(dataPedido)
        .status(statusPedido)
        .valorTotal(valorTotal)
        .build();
  }

  public static Endereco criaEndereco(UUID idEndereco) {
    return Endereco.builder()
        .id(idEndereco)
        .logradouro("Rua Teste")
        .numero("123")
        .bairro("Centro")
        .cidade("São Paulo")
        .estado("SP")
        .cep("01000-000")
        .principal(true)
        .build();
  }

  public static EnderecoEntregaRequest criaEnderecoEntregaRequest(UUID idEndereco) {
    return EnderecoEntregaRequest.builder().idEnderecoEntrega(idEndereco).build();
  }

  public static Pedido criaPedido(StatusPedido status, Cliente cliente, Endereco enderecoAtual) {
    return Pedido.builder()
        .id(UUID.randomUUID())
        .status(status)
        .cliente(cliente)
        .enderecoEntrega(enderecoAtual)
        .build();
  }
}
