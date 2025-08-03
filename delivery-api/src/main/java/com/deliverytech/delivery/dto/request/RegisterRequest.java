package com.deliverytech.delivery.dto.request;

import com.deliverytech.delivery.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class RegisterRequest {

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Schema(description = "Email válido para login", example = "joao.silva@email.com")
    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Schema(description = "Senha para autenticação (mínimo 6 caracteres)", example = "senhaSegura123")
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha;

    @Schema(description = "Perfil (role) do usuário", example = "CLIENTE")
    @NotNull(message = "Role é obrigatória")
    private Role role;

    @Schema(description = "ID do restaurante associado, se aplicável", example = "10", nullable = true)
    private Long restauranteId; // Pode ser null
}
