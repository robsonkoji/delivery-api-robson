package com.deliverytech.delivery.security;

import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtils {

    private static UsuarioRepository usuarioRepository;

    // Método para injetar o repository (ex: via config ou setter)
    public static void setUsuarioRepository(UsuarioRepository repo) {
        usuarioRepository = repo;
    }

    /**
     * Retorna o objeto Usuario do usuário logado.
     * @return Usuario
     */
    public static Usuario getCurrentUser() {
        String email = getCurrentUsername();
        if (email == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado"));
    }

    /**
     * Retorna o ID do usuário logado.
     * @return Long ID do usuário
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Retorna o username (geralmente email) do usuário logado.
     */
    private static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }

    /**
     * Verifica se o usuário logado possui a role passada (exemplo: "ADMIN").
     * @param role String role sem o prefixo "ROLE_"
     * @return boolean
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
