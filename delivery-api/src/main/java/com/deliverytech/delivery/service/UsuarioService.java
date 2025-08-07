package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.entity.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(RegisterRequest request);
    boolean existsByEmail(String email);
    Usuario getUsuarioLogado();
}
