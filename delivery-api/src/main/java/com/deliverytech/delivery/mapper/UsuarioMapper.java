package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.response.UserResponse;
import com.deliverytech.delivery.entity.Usuario;

public final class UsuarioMapper {

    // Construtor privado para evitar instanciação
    private UsuarioMapper() {
        throw new UnsupportedOperationException("Classe utilitária não pode ser instanciada.");
    }

    public static UserResponse toResponse(Usuario usuario) {
        return UserResponse.of(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().name()
        );
    }
}
