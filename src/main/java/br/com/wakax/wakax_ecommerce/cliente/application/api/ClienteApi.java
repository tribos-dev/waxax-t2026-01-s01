package br.com.wakax.wakax_ecommerce.cliente.application.api;

import java.util.UUID;

import javax.validation.Valid;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteBuscaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;

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

  @PatchMapping("/desativar/{idCliente}")
  @ResponseStatus(HttpStatus.OK)
  ClienteResponse desativaCliente(@PathVariable UUID idCliente);

  @PatchMapping("/{idCliente}")
  ClienteAtualizaResponse atualizarCliente(
      @PathVariable UUID idCliente, @RequestBody @Valid ClienteAtualizaRequest clienteRequest);

  @PatchMapping("/{idCliente}/ativar")
  @ResponseStatus(HttpStatus.OK)
  ClienteResponse ativarCliente(@PathVariable UUID idCliente);

  @PatchMapping("/{idCliente}/inativar")
  @ResponseStatus(HttpStatus.OK)
  ClienteResponse inativarCliente(@PathVariable UUID idCliente);

  @GetMapping("/busca")
  @ResponseStatus(HttpStatus.OK)
  PageResponse<ClienteListResponse> buscarClientePorCriterios(
          @ModelAttribute @Valid ClienteBuscaRequest filtro);
}
