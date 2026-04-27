package br.com.wakax.wakax_ecommerce.pedido.application.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.pedido.application.api.request.PedidoRequest;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.PedidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.application.api.response.ProdutoMaisVendidoResponse;
import br.com.wakax.wakax_ecommerce.pedido.domain.StatusPedido;

@RestController
@RequestMapping("/pedido")
public interface PedidoAPI {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  PedidoResponse cadastraPedido(@Valid @RequestBody PedidoRequest pedidoRequest);

  @GetMapping("/{idPedido}")
  PedidoResponse buscaPedidoPorId(@PathVariable UUID idPedido);

  @GetMapping("/cliente/{idCliente}")
  PedidoPageResponse buscaPedidosDoCliente(
      @PathVariable("idCliente") UUID idCliente,
      @RequestParam(value = "status", required = false) StatusPedido statusPedido,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size);

  @GetMapping("/relatorios/produtos-mais-vendidos")
  List<ProdutoMaisVendidoResponse> geraRelatorioProdutosMaisVendidos(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
      @RequestParam(defaultValue = "10") Integer limite);
}
