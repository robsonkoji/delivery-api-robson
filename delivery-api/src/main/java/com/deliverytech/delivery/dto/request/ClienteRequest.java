package com.deliverytech.delivery.dto.request;

import com.deliverytech.delivery.validation.ValidCEP;
import com.deliverytech.delivery.validation.ValidTelefone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Objeto de requisição para cadastro de cliente")
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    @Schema(description = "Nome completo do cliente", example = "cliente1")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Endereço de email do cliente", example = "cliente1@email.com")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone
    @Size(min = 10, max = 20)
    @Schema(description = "Número de telefone do cliente", example = "(11) 91234-5678")
    private String telefone;

    /*@NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    @Schema(description = "Senha do cliente para acesso à plataforma", example = "minhasenha123")
    private String senha;*/

    @NotBlank(message = "Endereço é obrigatório")
    @ValidCEP
    @Schema(description = "Endereço residencial do cliente", example = "endereco1, 123, cep 10000-000")
    private String endereco;

    public String getEmail() {
        return email != null ? email.toLowerCase() : null;
    }

    public ClienteRequest(String nome, String email, String telefone, String endereco) {
    this.nome = nome;
    this.email = email;
    this.telefone = telefone;
    this.endereco = endereco;
    }
}
