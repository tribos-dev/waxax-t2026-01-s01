package br.com.wakax.wakax_ecommerce.carrinho.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;
import br.com.wakax.wakax_ecommerce.estoque.domain.Estoque;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrinho {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  @NotNull
  private Cliente cliente;

  @Column(nullable = false)
  @NotNull
  private LocalDateTime dataCriacao;

  @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
  @NotNull
  private List<ItemCarrinho> itensCarrinho;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatusCarrinho statusCarrinho;

  public Carrinho(Cliente cliente) {
    this.cliente = cliente;
    this.dataCriacao = LocalDateTime.now();
    this.statusCarrinho = StatusCarrinho.ATIVO;
    this.itensCarrinho = new ArrayList<>();
  }

  public void adicionaItemAoCarrinho(ItemCarrinhoRequest itemCarrinhoRequest, Produto produto) {
    verificaSeCarrinhoEstaAtivo();
    ItemCarrinho novoItem = new ItemCarrinho(this, produto, itemCarrinhoRequest);
    this.itensCarrinho.add(novoItem);
  }

  public ItemCarrinho buscaItemPorId(UUID idItem) {
    return this.itensCarrinho.stream()
        .filter(item -> item.getId().equals(idItem))
        .findFirst()
        .orElseThrow(
            () -> new APIException(HttpStatus.NOT_FOUND, ErrorCode.ITEM_CARRINHO_NAO_ENCONTRADO));
  }

  void verificaSeCarrinhoEstaAtivo() {
    if (!this.statusCarrinho.equals(StatusCarrinho.ATIVO)) {
      throw new APIException(HttpStatus.CONFLICT, ErrorCode.CARRINHO_NAO_ATIVO);
    }
  }

  public void removeItem(UUID idItem, String email) {

    verificaSeCarrinhoPertenceAoUsuario(email);
    verificaSeCarrinhoEstaAtivo();

    ItemCarrinho item =
        this.itensCarrinho.stream()
            .filter(i -> i.getId().equals(idItem))
            .findFirst()
            .orElseThrow(
                () ->
                    new APIException(HttpStatus.NOT_FOUND, ErrorCode.ITEM_CARRINHO_NAO_ENCONTRADO));

    this.itensCarrinho.remove(item);
  }

  public BigDecimal calculaValorTotal() {
    return itensCarrinho.stream()
        .map(ItemCarrinho::getValorTotalDoItem)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void finalizar() {
    verificaSeCarrinhoEstaAtivo();
    this.statusCarrinho = StatusCarrinho.FINALIZADO;
  }

  void verificaSeCarrinhoPertenceAoUsuario(String email) {
    boolean pertenceAoUsuario = this.cliente.getPessoa().getEmails().contains(email);

    if (!pertenceAoUsuario) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.ITEM_CARRINHO_NAO_ENCONTRADO);
    }
  }

  public void ativar() {
    this.statusCarrinho = StatusCarrinho.ATIVO;
  }

    public void validaAlteracaoDeQuantidadeDoItem(UUID idCliente, UUID idItem, Integer quantidade, Estoque estoque) {
      verificaSeCarrinhoPertenceAoCliente(idCliente);
      verificaSeCarrinhoEstaAtivo();
      buscaItemPorId(idItem);
      validaQuantidade(quantidade, estoque);
  }

  private void verificaSeCarrinhoPertenceAoCliente(UUID idCliente) {
    if (cliente == null || !this.cliente.getId().equals(idCliente)) {
      throw new APIException(
          HttpStatus.FORBIDDEN, ErrorCode.CARRINHO_NAO_PERTENCE_AO_CLIENTE_AUTENTICADO);
    }
  }

  private void validaQuantidade(Integer quantidade, Estoque estoque) {
    validaQuantidadeMinima(quantidade);
    validaSeExisteQuantidadeEmEstoque(quantidade, estoque);
  }

  private void validaSeExisteQuantidadeEmEstoque(Integer quantidade, Estoque estoque) {
    if (!estoque.temQuantidadeDisponivel(quantidade)) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.QUANTIDADE_INSUFICIENTE_ESTOQUE);
    }
  }

  private void validaQuantidadeMinima(Integer quantidade) {
    if (quantidade < 1) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.QUANTIDADE_INVALIDA);
    }
  }


}
