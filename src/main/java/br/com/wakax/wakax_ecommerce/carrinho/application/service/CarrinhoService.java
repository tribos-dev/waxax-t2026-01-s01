package br.com.wakax.wakax_ecommerce.carrinho.application.service;

import java.util.List;
import java.util.UUID;

import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;

public interface CarrinhoService {
  CarrinhoResponse adicionaItemNoCarrinho(UUID idCliente, ItemCarrinhoRequest itemCarrinho);

  CarrinhoResponse buscaCarrinhoPorId(UUID idCliente, UUID idCarrinho);

  List<CarrinhosListAllResponse> buscarTodosOsCarrinhos(UUID idCliente);
}
