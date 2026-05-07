package br.com.wakax.wakax_ecommerce.produto.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;

import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaRequest;
import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
  @Id @GeneratedValue private UUID id;

  @Column(length = 150, nullable = false)
  @NotNull
  @Size(max = 150)
  private String descricao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private StatusProduto status;

  @Column(nullable = false)
  @NotNull
  private BigDecimal pesoLiquido;

  @Column(nullable = false)
  @NotNull
  private BigDecimal pesoBruto;

  @Column(length = 500)
  @Size(max = 500)
  private String descricaoComplementar;

  @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Preco> precos;

  @Column(length = 50)
  @Size(max = 50)
  private String grupo;

  @Column(length = 10)
  @Size(max = 10)
  private String unidade;

  private Integer estoqueMinimo;
  private Integer estoqueMaximo;

  @Column(name = "data_de_cadastro")
  private LocalDateTime dataDeCadastro;

  @Column(name = "data_de_atualizacao")
  private LocalDateTime dataDeAtualizacao;

  @Column(name = "data_de_alteracao_status")
  private LocalDateTime dataAlteracaoStatus;

  @Column(length = 255)
  private String motivoAlteracao;

  public Produto(ProdutoRequest request) {
    this.descricao = request.getDescricao();
    this.status = StatusProduto.ATIVO;
    this.pesoLiquido = request.getPesoLiquido();
    this.pesoBruto = request.getPesoBruto();
    this.descricaoComplementar = request.getDescricaoComplementar();
    this.grupo = request.getGrupo();
    this.unidade = request.getUnidade();
    this.estoqueMinimo = request.getEstoqueMinimo();
    this.estoqueMaximo = request.getEstoqueMaximo();
    if (request.getPrecos() != null) {
      this.precos =
          request.getPrecos().stream()
              .map(precoReq -> new Preco(precoReq.getTipo(), precoReq.getValor(), this))
              .collect(Collectors.toList());
    }
    this.dataDeCadastro = LocalDateTime.now();
  }

  public void atualiza(ProdutoAtualizaRequest request) {
    atualizaSeNaoNulo(request.getDescricao(), valor -> this.descricao = valor);
    atualizaSeNaoNulo(request.getPesoLiquido(), valor -> this.pesoLiquido = valor);
    atualizaSeNaoNulo(request.getPesoBruto(), valor -> this.pesoBruto = valor);
    atualizaSeNaoNulo(
        request.getDescricaoComplementar(), valor -> this.descricaoComplementar = valor);
    atualizaSeNaoNulo(request.getGrupo(), valor -> this.grupo = valor);
    atualizaSeNaoNulo(request.getUnidade(), valor -> this.unidade = valor);
    atualizaSeNaoNulo(request.getEstoqueMinimo(), valor -> this.estoqueMinimo = valor);
    atualizaSeNaoNulo(request.getEstoqueMaximo(), valor -> this.estoqueMaximo = valor);
    if (request.getPrecos() != null) {
      if (this.precos == null) {
        this.precos = new ArrayList<>();
      }
      request
          .getPrecos()
          .forEach(
              precoReq ->
                  this.precos.stream()
                      .filter(p -> p.getTipo().equals(precoReq.getTipo()))
                      .findFirst()
                      .ifPresentOrElse(
                          existente -> existente.setValor(precoReq.getValor()),
                          () ->
                              this.precos.add(
                                  new Preco(precoReq.getTipo(), precoReq.getValor(), this))));
    }
    if (this.pesoLiquido != null
        && this.pesoBruto != null
        && this.pesoLiquido.compareTo(this.pesoBruto) > 0) {
      throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.PESO_LIQUIDO_MAIOR_QUE_BRUTO);
    }
    this.dataDeAtualizacao = LocalDateTime.now();
  }

  private <T> void atualizaSeNaoNulo(T valor, Consumer<T> setter) {
    if (valor != null) {
      setter.accept(valor);
    }
  }

  public BigDecimal getPrecoPadrao() {
    if (this.precos.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return this.precos.get(0).getValor();
  }

  public BigDecimal getPrecoAtual() {
    if (this.precos == null || this.precos.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return this.precos.stream()
        .filter(preco -> preco.getTipo() == TipoPreco.PADRAO)
        .map(Preco::getValor)
        .findFirst()
        .orElse(BigDecimal.ZERO);
  }

  public void alteraStatus(StatusProduto novoStatus, String motivo) {
    if (this.status == novoStatus) {
      ErrorCode errorCode =
          novoStatus == StatusProduto.ATIVO
              ? ErrorCode.PRODUTO_JA_ATIVO
              : ErrorCode.PRODUTO_JA_INATIVO;
      throw new APIException(HttpStatus.CONFLICT, errorCode);
    }
    this.status = novoStatus;
    this.dataAlteracaoStatus = LocalDateTime.now();
    this.motivoAlteracao = motivo;
  }

  public void validaDisponibilidade() {
    if (this.status == StatusProduto.INATIVO) {
      throw new APIException(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.PRODUTO_INATIVO);
    }
  }
}
