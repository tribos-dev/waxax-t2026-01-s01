package br.com.wakax.wakax_ecommerce.carrinho.application.service;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;

import java.util.List;
import java.util.UUID;

public interface CarrinhoService {
  CarrinhoResponse adicionaItemNoCarrinho(UUID idCliente, ItemCarrinhoRequest itemCarrinho);

  CarrinhoResponse buscaCarrinhoPorId(UUID idCliente, UUID idCarrinho);

  List<CarrinhosListAllResponse> buscarTodosOsCarrinhos(UUID idCliente);

  void deletaItemDoCarrinho(String token, UUID idCarrinho, UUID idProduto);

  void restaurarCarrinho(UUID id);
}
