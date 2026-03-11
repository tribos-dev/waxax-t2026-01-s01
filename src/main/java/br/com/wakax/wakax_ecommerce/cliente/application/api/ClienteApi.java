package br.com.wakax.wakax_ecommerce.cliente.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteAtualizaResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteListAllResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.PageResponse;

@RestController
@RequestMapping("/cliente")
public interface ClienteApi {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ClienteResponse cadastrarCliente(@RequestBody @Valid ClienteRequest clienteRequest);

  @GetMapping("/{idCliente}")
  ClienteResponse buscaClienteEspecifico(@PathVariable UUID idCliente);

  @GetMapping("/clientes")
  PageResponse<ClienteListAllResponse> buscarTodosOsClientes(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

  @PatchMapping("/{idCliente}")
  ClienteAtualizaResponse atualizarCliente(
      @PathVariable UUID idCliente, @RequestBody @Valid ClienteAtualizaRequest clienteRequest);
}
