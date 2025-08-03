package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entity.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public Usuario getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }
        throw new RuntimeException("Usuário não autenticado");
    }
}
