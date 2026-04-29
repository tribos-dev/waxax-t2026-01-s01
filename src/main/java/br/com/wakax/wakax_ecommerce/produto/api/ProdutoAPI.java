package br.com.wakax.wakax_ecommerce.produto.api;

import java.util.UUID;

import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoAtualizaRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoAtualizaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.produto.api.request.ProdutoRequest;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoListagemResponse;
import br.com.wakax.wakax_ecommerce.produto.api.response.ProdutoResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/produto")
public interface ProdutoAPI {
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ProdutoResponse cadastraProduto(@RequestBody ProdutoRequest novoProduto);

  @GetMapping("/{idProduto}")
  ProdutoListResponse buscaProdutoPorId(@PathVariable UUID idProduto);

  @GetMapping("/produtos")
  @ResponseStatus(HttpStatus.OK)
  ProdutoListagemResponse listarTodosProdutos(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

  @PatchMapping("/{idProduto}")
  @ResponseStatus(HttpStatus.OK)
  ProdutoAtualizaResponse atualizarProduto(@PathVariable UUID idProduto,
                                           @RequestBody @Valid ProdutoAtualizaRequest atualizaRequest);
}
