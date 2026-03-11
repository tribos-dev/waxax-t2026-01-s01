package br.com.wakax.wakax_ecommerce.carrinho.api;

import br.com.wakax.wakax_ecommerce.auth.security.service.TokenService;
import br.com.wakax.wakax_ecommerce.carrinho.api.request.ItemCarrinhoRequest;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhoResponse;
import br.com.wakax.wakax_ecommerce.carrinho.api.response.CarrinhosListAllResponse;
import br.com.wakax.wakax_ecommerce.carrinho.application.service.CarrinhoService;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

  private String getUsuarioByToken(String token) {
    log.info("[token] {}", token);
    String usuario = tokenService.getUsuarioByBearerToken(token).orElseThrow
            (() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
    log.info("[usuario] {}", usuario);
    return usuario;
  }
}
