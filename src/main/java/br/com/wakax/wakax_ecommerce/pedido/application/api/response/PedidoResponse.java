package br.com.wakax.wakax_ecommerce.pedido.application.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.wakax.wakax_ecommerce.pedido.domain.FormaPagamento;
import br.com.wakax.wakax_ecommerce.pedido.domain.ItemPedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.Pedido;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;
import lombok.Getter;

@Getter
public class PedidoResponse {
  private UUID idPedido;
  private UUID clienteId;
  private String nomeCliente;
  private LocalDateTime dataPedido;
  private StatusPedido status;
  private List<ItemPedidoResponse> itensPedido;
  private BigDecimal valorTotal;
  private FormaPagamento formaPagamento;
  private EnderecoResponse enderecoEntrega;

  public PedidoResponse(Pedido pedido) {
    this.idPedido = pedido.getId();
    this.clienteId = pedido.getCliente().getId();
    this.nomeCliente = pedido.getCliente().getPessoa().getNome();
    this.dataPedido = pedido.getDataPedido();
    this.status = pedido.getStatus();
    this.itensPedido =
        pedido.getItensPedido().stream().map(ItemPedidoResponse::new).collect(Collectors.toList());
    this.valorTotal = pedido.getValorTotal();
    this.formaPagamento = pedido.getFormaPagamento();
    this.enderecoEntrega = new EnderecoResponse(pedido.getEnderecoEntrega());
  }

  @Getter
  public static class ItemPedidoResponse {
    private UUID id;
    private UUID produtoId;
    private String descricaoProduto;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ItemPedidoResponse(ItemPedido item) {
      this.id = item.getId();
      this.produtoId = item.getProduto().getId();
      this.descricaoProduto = item.getProduto().getDescricao();
      this.quantidade = item.getQuantidade();
      this.valorUnitario = item.getValorUnitario();
      this.valorTotal = item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
    }
  }

  @Getter
  public static class EnderecoResponse {
    private UUID id;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public EnderecoResponse(br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco endereco) {
      this.id = endereco.getId();
      this.logradouro = endereco.getLogradouro();
      this.numero = endereco.getNumero();
      this.complemento = endereco.getComplemento();
      this.bairro = endereco.getBairro();
      this.cidade = endereco.getCidade();
      this.estado = endereco.getEstado();
      this.cep = endereco.getCep();
    }
  }
}
