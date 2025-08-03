package com.deliverytech.delivery.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @Schema(description = "Email do usuário para login", example = "usuario@exemplo.com")
    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @Schema(description = "Senha do usuário para login", example = "senhaSegura123")
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
