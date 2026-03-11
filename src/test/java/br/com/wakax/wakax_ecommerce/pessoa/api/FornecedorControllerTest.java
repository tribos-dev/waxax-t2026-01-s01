
package br.com.wakax.wakax_ecommerce.pessoa.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.response.FornecedorAtualizaResponse;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.wakax.wakax_ecommerce.fornecedor.application.api.FornecedorController;
import br.com.wakax.wakax_ecommerce.fornecedor.application.api.request.FornecedorAtualizaRequest;
import br.com.wakax.wakax_ecommerce.fornecedor.application.service.FornecedorService;

@ExtendWith(MockitoExtension.class)
class FornecedorControllerTest {

    @Mock
    private FornecedorService fornecedorService;

    @InjectMocks
    private FornecedorController fornecedorController;

    private Validator validator;

    private UUID fornecedorId;
    private FornecedorAtualizaRequest request;

    @BeforeEach
    void init() {
        // Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Dados padrão válidos
        fornecedorId = UUID.randomUUID();

        request = new FornecedorAtualizaRequest(
                List.of("email@email.com"),
                List.of("11999999999"),
                List.of(mockEndereco()),
                "12345",
                "Razão Social",
                "Nome Fantasia"
        );
    }

    private Endereco mockEndereco() {
        return new Endereco();
    }

    @Test
    void deveAtualizarFornecedorComSucesso() {

        FornecedorAtualizaResponse responseMock = mock(FornecedorAtualizaResponse.class);

        when(fornecedorService.atualizarFornecedor(fornecedorId, request))
                .thenReturn(responseMock);

        var response = fornecedorController.atualizarFornecedor(fornecedorId, request);

        assertNotNull(response);
        assertEquals(responseMock, response);

        verify(fornecedorService, times(1))
                .atualizarFornecedor(fornecedorId, request);
    }

    @Test
    void deveLancarExcecaoAoAtualizarQuandoFornecedorNaoEncontrado() {

        RuntimeException exception = new RuntimeException("Fornecedor não encontrado");

        when(fornecedorService.atualizarFornecedor(fornecedorId, request))
                .thenThrow(exception);

        assertThrows(RuntimeException.class,
                () -> fornecedorController.atualizarFornecedor(fornecedorId, request));

        verify(fornecedorService, times(1))
                .atualizarFornecedor(fornecedorId, request);
    }

    @Test
    void deveFalharQuandoEmailsForemNulos() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                null,
                List.of("11999999999"),
                List.of(mockEndereco()),
                "123",
                "Razão Social",
                "Nome Fantasia"
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }


    @Test
    void deveFalharQuandoEmailForInvalido() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                List.of("email-invalido"),
                List.of("11999999999"),
                List.of(mockEndereco()),
                "123",
                "Razão Social",
                "Nome Fantasia"
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoTelefonesFoenrNulos() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                List.of("email@email.com"),
                null,
                List.of(mockEndereco()),
                "123",
                "Razão Social",
                "Nome Fantasia"
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoTelefoneForInvalido() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                List.of("email@email.com"),
                List.of("123"),
                List.of(mockEndereco()),
                "123",
                "Razão Social",
                "Nome Fantasia"
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoRazaoSocialForBlank() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                List.of("email@email.com"),
                List.of("11999999999"),
                List.of(mockEndereco()),
                "123",
                "",
                "Nome Fantasia"
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }

    @Test
    void deveFalharQuandoNomeFantasiaForBlank() {

        FornecedorAtualizaRequest requestInvalido = new FornecedorAtualizaRequest(
                List.of("email@email.com"),
                List.of("11999999999"),
                List.of(mockEndereco()),
                "123",
                "Razão Social",
                ""
        );

        Set<ConstraintViolation<FornecedorAtualizaRequest>> violations =
                validator.validate(requestInvalido);

        assertFalse(violations.isEmpty());
    }
}