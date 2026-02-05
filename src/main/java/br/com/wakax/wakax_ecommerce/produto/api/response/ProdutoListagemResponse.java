package br.com.wakax.wakax_ecommerce.produto.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import br.com.wakax.wakax_ecommerce.produto.domain.Preco;
import br.com.wakax.wakax_ecommerce.produto.domain.Produto;
import br.com.wakax.wakax_ecommerce.produto.domain.StatusProduto;
import br.com.wakax.wakax_ecommerce.produto.domain.TipoPreco;
import lombok.Data;

@Data
public class ProdutoListagemResponse {

  private final List<ProdutoItem> produtos;
  private final long totalProdutos;

  public ProdutoListagemResponse(List<ProdutoItem> produtos) {
    this.produtos = produtos;
    this.totalProdutos = produtos.size();
  }

  public ProdutoListagemResponse(List<ProdutoItem> produtos, long totalProdutos) {
    this.produtos = produtos;
    this.totalProdutos = totalProdutos;
  }

  public static ProdutoListagemResponse converte(List<Produto> produtos) {
    List<ProdutoItem> produtosOrdenados =
        produtos.stream()
            .map(ProdutoItem::new)
            .sorted(Comparator.comparing(ProdutoItem::getDescricao))
            .toList();

    return new ProdutoListagemResponse(produtosOrdenados);
  }

  public static ProdutoListagemResponse convertePaginado(
      List<Produto> produtos, long totalElementos) {
    List<ProdutoItem> produtosConvertidos = produtos.stream().map(ProdutoItem::new).toList();

    return new ProdutoListagemResponse(produtosConvertidos, totalElementos);
  }

  @Data
  public static class ProdutoItem {
    private final StatusProduto status;
    private final String descricao;
    private final LocalDateTime dataDeCadastro;
    private final BigDecimal precoAtual;

    public ProdutoItem(Produto produto) {
      this.status = produto.getStatus();
      this.descricao = produto.getDescricao();
      this.dataDeCadastro = produto.getDataDeCadastro();
      this.precoAtual = obterPrecoAtual(produto);
    }

    private BigDecimal obterPrecoAtual(Produto produto) {
      if (produto.getPrecos() == null || produto.getPrecos().isEmpty()) {
        return BigDecimal.ZERO;
      }
      return produto.getPrecos().stream()
          .filter(preco -> preco.getTipo() == TipoPreco.PADRAO)
          .map(Preco::getValor)
          .findFirst()
          .orElse(BigDecimal.ZERO);
    }
  }
}
