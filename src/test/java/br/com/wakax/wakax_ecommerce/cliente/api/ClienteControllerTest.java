package br.com.wakax.wakax_ecommerce.cliente.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import br.com.wakax.wakax_ecommerce.cliente.application.api.ClienteController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.wakax.wakax_ecommerce.cliente.application.api.response.ClienteListAllResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.api.response.PageResponse;
import br.com.wakax.wakax_ecommerce.cliente.application.service.ClienteService;
import br.com.wakax.wakax_ecommerce.cliente.domain.Cliente;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @Test
    void deveListarClientesComSucesso() {
        PageRequest pageable = PageRequest.of(0, 10);
        Cliente cliente = mock(Cliente.class);
        Page<Cliente> page = new PageImpl<>(List.of(cliente), pageable, 1);
        when(clienteService.buscarTodosOsClientes(pageable)).thenReturn(page);
        PageResponse<ClienteListAllResponse> response = clienteController.buscarTodosOsClientes(0, 10);
        assertNotNull(response);
        assertEquals(1, response.getTotalDeUsuarios());
        assertEquals(1, response.getContent().size());
        verify(clienteService, times(1)).buscarTodosOsClientes(pageable);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirClientes() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Cliente> pageVazia = new PageImpl<>(List.of(), pageable, 0);
        when(clienteService.buscarTodosOsClientes(pageable)).thenReturn(pageVazia);
        PageResponse<ClienteListAllResponse> response = clienteController.buscarTodosOsClientes(0, 10);
        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
        assertEquals(0, response.getTotalDeUsuarios());
        verify(clienteService, times(1)).buscarTodosOsClientes(pageable);
    }
}
