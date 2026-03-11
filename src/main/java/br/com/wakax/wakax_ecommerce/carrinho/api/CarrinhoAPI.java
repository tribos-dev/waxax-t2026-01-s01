package br.com.wakax.wakax_ecommerce.carrinho.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;

@RestController
@RequestMapping("/carrinho")
public interface CarrinhoAPI {

  @PostMapping("/{idCliente}")
  @ResponseStatus(code = HttpStatus.CREATED)
  CarrinhoResponse adicionaItemNoCarrinho(
      @PathVariable("idCliente") UUID idCliente,
      @Valid @RequestBody ItemCarrinhoRequest itemCarrinho);

  @GetMapping("{idCliente}/busca-carrinho/{idCarrinho}")
  CarrinhoResponse buscaCarrinhoPorId(
      @PathVariable("idCliente") UUID idCliente, @PathVariable("idCarrinho") UUID idCarrinho);

  @GetMapping("{idCliente}/busca-carrinhos")
  List<CarrinhosListAllResponse> buscarTodosOsCarrinhos(@PathVariable("idCliente") UUID idCliente);

  @DeleteMapping("/{idCarrinho}/remove-item/{idItem}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  void deletaItemDoCarrinho(
      @RequestHeader(name = "Authorization", required = true) String token,
      @PathVariable UUID idCarrinho,
      @PathVariable UUID idItem);
}
