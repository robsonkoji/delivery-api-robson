package com.deliverytech.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidade que representa um produto disponível em um restaurante.")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do produto", example = "101")
    private Long id;

    @NotBlank
    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String nome;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Descrição detalhada do produto", example = "Pizza tradicional com molho de tomate, mussarela e manjericão fresco.")
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Schema(description = "Preço unitário do produto", example = "49.90")
    private BigDecimal preco;

    @NotBlank
    @Schema(description = "Categoria do produto", example = "Pizzas")
    private String categoria;

    @Schema(description = "Indica se o produto está disponível para pedidos", example = "true")
    private boolean disponivel;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    @Schema(description = "Restaurante ao qual o produto pertence")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "produto")
    @JsonIgnore
    private List<ItemPedido> itensPedido;

    public Boolean getDisponivel() {
        return disponivel;
    }

    public boolean getAtivo() {
        return this.disponivel;
    }

    public void setAtivo(Boolean ativo) {
        this.disponivel = ativo;
    }

    private int estoque;

    // Getter
    public int getEstoque() {
        return estoque;
    }

    // Setter (implemente assim)
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
