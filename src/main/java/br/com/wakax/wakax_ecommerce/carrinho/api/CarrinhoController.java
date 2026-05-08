package br.com.wakax.wakax_ecommerce.carrinho.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.wakax.wakax_ecommerce.auth.security.service.TokenService;
import br.com.wakax.wakax_ecommerce.carrinho.api.request.AlteraQuantidadeDeItemRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;
import br.com.wakax.wakax_ecommerce.carrinho.application.service.CarrinhoService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CarrinhoController implements CarrinhoAPI {

  private final CarrinhoService carrinhoService;
  private final TokenService tokenService;

  @Override
  public CarrinhoResponse adicionaItemNoCarrinho(UUID idCliente, ItemCarrinhoRequest itemCarrinho) {
    log.info("[start] CarrinhoController - adicionaItemNoCarrinho");
    CarrinhoResponse carrinho = carrinhoService.adicionaItemNoCarrinho(idCliente, itemCarrinho);
    log.debug("[finish] CarrinhoController - adicionaItemNoCarrinho");
    return carrinho;
  }

  @Override
  public CarrinhoResponse buscaCarrinhoPorId(UUID idCliente, UUID idCarrinho) {
    log.info("[start] CarrinhoController - buscaCarrinhoPorId");
    CarrinhoResponse carrinho = carrinhoService.buscaCarrinhoPorId(idCliente, idCarrinho);
    log.debug("[finish] CarrinhoController - buscaCarrinhoPorId");
    return carrinho;
  }

  @Override
  public List<CarrinhosListAllResponse> buscarTodosOsCarrinhos(UUID idCliente) {
    log.info("[start] CarrinhoController - buscarTodosOsCarrinhos");
    List<CarrinhosListAllResponse> carrinhos = carrinhoService.buscarTodosOsCarrinhos(idCliente);
    log.debug("[finish] CarrinhoController - buscarTodosOsCarrinhos");
    return carrinhos;
  }

  @Override
  public void deletaItemDoCarrinho(String token, UUID idCarrinho, UUID idItem) {
    log.info("[start] CarrinhoController - deletaItemDoCarrinho");
    String usuario = getUsuarioByToken(token);
    carrinhoService.deletaItemDoCarrinho(usuario, idCarrinho, idItem);
    log.info("[finish] CarrinhoController - deletaItemDoCarrinho");
  }

  @Override
  public void alteraQuantidadeDeItem(
      UUID idCarrinho, UUID idItem, UUID idCliente, AlteraQuantidadeDeItemRequest request) {
    log.info("[start] CarrinhoController - alteraQuantidadeDeItem");
    carrinhoService.alteraQuantidadeDeItem(idCarrinho, idItem, idCliente, request);
    log.debug("[finish] CarrinhoController - alteraQuantidadeDeItem");
  }

  private String getUsuarioByToken(String token) {
    log.info("[token] {}", token);
    String usuario =
        tokenService
            .getUsuarioByBearerToken(token)
            .orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
    log.info("[usuario] {}", usuario);
    return usuario;
  }
}
