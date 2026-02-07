package br.com.wakax.wakax_ecommerce.estoque.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueListagemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.estoque.api.request.EstoqueRequest;
import br.com.wakax.wakax_ecommerce.estoque.api.response.EstoqueResponse;

@RestController
@RequestMapping("/estoque")
public interface EstoqueAPI {

  @PostMapping("/produto/{idProduto}")
  @ResponseStatus(HttpStatus.CREATED)
  EstoqueResponse criaEstoque(
      @PathVariable UUID idProduto, @Valid @RequestBody EstoqueRequest request);

  @GetMapping("/produto/{idProduto}")
  EstoqueResponse buscaEstoquePorIdProduto(@PathVariable UUID idProduto);

  @GetMapping
  ResponseEntity<EstoqueListagemResponse> listarTodoEstoque(
          @RequestParam(value = "quantidadeMinima", required = false) Integer quantidadeMinima,
          @RequestParam(value = "apenasEmFalta", required = false) Boolean apenasEmFalta);
}
