package br.com.wakax.wakax_ecommerce.pedido.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.pedido.domain.*;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;

public class RastreamentoDataHelper {
  public static Pedido criaPedidoValido() {
    return Pedido.builder()
        .id(UUID.fromString("5b47c6e0-2f1a-4d3c-9b8a-7e6d5c4b3a21"))
        .cliente(criaClienteValido())
        .enderecoEntrega(criaEnderecoValido())
        .formaPagamento(FormaPagamento.PIX)
        .status(StatusPedido.PAGO)
        .valorTotal(new BigDecimal("299.99"))
        .dataPedido(LocalDateTime.now())
        .itensPedido(new ArrayList<>())
        .build();
  }

  public static Cliente criaClienteValido() {
    return Cliente.builder()
        .id(UUID.fromString("b2c3d4e5-1234-5678-9abc-def123456789"))
        .pessoa(Pessoa.builder().emails(List.of("cliente1@gmail.com")).build())
        .build();
  }

  public static Endereco criaEnderecoValido() {
    return Endereco.builder()
        .id(UUID.fromString("d4e5f6a7-1234-5678-9abc-def123456789"))
        .cep("64800-370")
        .logradouro("Av. Eurípedes de Aguiar")
        .numero("1514")
        .complemento("Apto C")
        .bairro("Irapua")
        .cidade("Floriano")
        .estado("PI")
        .build();
  }

  public static Rastreamento criaRastremanento() {
    return createRastreamento(
        "WAX123456", "SEDEX", StatusRastreamento.EM_TRANSITO, LocalDate.now().plusDays(2));
  }

  private static Rastreamento createRastreamento(
      String codigo,
      String transportadora,
      StatusRastreamento statusAtual,
      LocalDate previsaoEntrega) {
    return Rastreamento.builder()
        .codigo(codigo)
        .transportadora(transportadora)
        .statusAtual(statusAtual)
        .previsaoEntrega(previsaoEntrega)
        .historico(criaHistoricoRastremanento())
        .build();
  }

  public static List<HistoricoRastreamento> criaHistoricoRastremanento() {
    return List.of(
        createHistorico(
            LocalDateTime.now().minusDays(3),
            "São Paulo, SP",
            "Objeto postado no CD Cajamar",
            StatusRastreamento.CRIADO),
        createHistorico(
            LocalDateTime.now().minusDays(1),
            "Teresina, PI",
            "Chegou na unidade de tratamento regional",
            StatusRastreamento.EM_TRANSITO));
  }

  private static HistoricoRastreamento createHistorico(
      LocalDateTime dataEvento, String local, String descricao, StatusRastreamento status) {
    return HistoricoRastreamento.builder()
        .dataEvento(dataEvento)
        .local(local)
        .descricao(descricao)
        .status(status)
        .build();
  }
}
