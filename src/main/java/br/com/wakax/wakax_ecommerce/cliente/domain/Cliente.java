package br.com.wakax.wakax_ecommerce.cliente.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import br.com.wakax.wakax_ecommerce.cliente.application.api.request.ClienteRequest;
import br.com.wakax.wakax_ecommerce.pessoa.domain.Pessoa;
import lombok.*;

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

    public void ativar() {
        this.status = StatusCliente.ATIVO;
        this.dataReativacao = LocalDateTime.now();
        this.pessoa.ativar();
    }

    public boolean isAtivo() {
        return StatusCliente.ATIVO.equals(this.status);
    }
}
