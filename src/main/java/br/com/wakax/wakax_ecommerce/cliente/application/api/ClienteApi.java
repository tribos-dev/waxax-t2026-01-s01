package br.com.wakax.wakax_ecommerce.cliente.application.api;

import java.util.UUID;

import javax.validation.Valid;

import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteListAllResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteResponse;

@RestController
@RequestMapping("/cliente")
public interface ClienteApi {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  ClienteResponse cadastrarCliente(@RequestBody @Valid ClienteRequest clienteRequest);

  @GetMapping("/{idCliente}")
  ClienteResponse buscaClienteEspecifico(@PathVariable UUID idCliente);

  @GetMapping("/ListarClientes")
  PageResponse<ClienteListAllResponse> buscarTodosOsClientes(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size);

}
