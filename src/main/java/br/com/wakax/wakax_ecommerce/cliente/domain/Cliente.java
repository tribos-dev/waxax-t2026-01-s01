package br.com.wakax.wakax_ecommerce.cliente.domain;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteAtualizaRequest;
import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.handler.APIException;
import br.com.wakax.wakax_ecommerce.handler.ErrorCode;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import br.com.wakax.wakax_ecommerce.pessoa.domain.StatusPessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(nullable = false, unique = true)
    @NotNull
    private Pessoa pessoa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCliente status;

    @Column(nullable = false, name = "data_criacao")
    @NotNull
    private LocalDateTime dataCriacao;

    @Column(nullable = false, name = "data_edicao")
    @NotNull
    private LocalDateTime dataEdicao;

    private LocalDateTime dataReativacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataEdicao = LocalDateTime.now();
        this.status = StatusCliente.INATIVO;
    }

    @PreUpdate
    protected void onUpdate() {
        dataEdicao = LocalDateTime.now();
    }

    public Cliente(ClienteRequest request) {
        this.pessoa = Pessoa.criarDe(request);
    }

    public void desativar(Pessoa pessoa) {
        pessoa.desativar();
        this.dataEdicao = LocalDateTime.now();
    }

    public void validaClienteAtivo() {
        if (this.pessoa.getStatus() == StatusPessoa.INATIVO) {
            throw APIException.build(
                    HttpStatus.CONFLICT, "Cliente está inativo e não pode realizar pagamentos");
        }
    }

    public void alterar(ClienteAtualizaRequest request) {
        this.pessoa.alterar(request);
        this.dataEdicao = LocalDateTime.now();
    }

    public void ativar() {
        this.status = StatusCliente.ATIVO;
        this.dataReativacao = LocalDateTime.now();
        this.pessoa.ativar();
    }

    public boolean isAtivo() {
        return StatusCliente.ATIVO.equals(this.status);
    }

    public void inativar() {
        this.status = StatusCliente.INATIVO;
        this.pessoa.inativar();
    }

    public boolean isInativo() {
        return StatusCliente.INATIVO.equals(this.status);
    }

    public void validaSeClienteEstaAtivo() {
        if (!this.isAtivo()) {
            throw new APIException(HttpStatus.FORBIDDEN, ErrorCode.CLIENTE_INATIVO);
        }
    }
}
