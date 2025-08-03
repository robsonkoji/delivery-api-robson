package com.deliverytech.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um restaurante na plataforma.")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do restaurante", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Nome do restaurante", example = "Cantina da Nonna")
    private String nome;

    @NotBlank
    @Schema(description = "Categoria culinária do restaurante", example = "Italiana")
    private String categoria;

    @NotBlank
    @Schema(description = "Endereço completo do restaurante", example = "Rua das Flores, 123 - Centro")
    private String endereco;

    @NotBlank
    @Schema(description = "Telefone de contato do restaurante", example = "(11) 99999-8888")
    private String telefone;

    @PastOrPresent
    @Schema(description = "Data de criação do cadastro", example = "2025-08-01T12:00:00")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Schema(description = "Indica se o restaurante está ativo para pedidos", example = "true")
    private Boolean ativo;

    @NotNull
    @PositiveOrZero
    @Column(name = "taxa_entrega")
    @Schema(description = "Valor da taxa de entrega", example = "8.50")
    private BigDecimal taxaEntrega;

    @Column(name = "avaliacao")
    @Schema(description = "Nota média de avaliação do restaurante", example = "4.7")
    private BigDecimal avaliacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @Schema(description = "Usuário responsável pelo restaurante")
    private Usuario usuario;

    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Pedido> pedidos;

    public void inativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }
}
