package com.deliverytech.delivery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.security.SecurityUtils;

import jakarta.annotation.PostConstruct;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email); // já deve existir

    boolean existsByEmail(String email); // <-- adicione esta linha

    @Component
    public class SecurityUtilsInjector {

        private final UsuarioRepository usuarioRepository;

        public SecurityUtilsInjector(UsuarioRepository usuarioRepository) {
            this.usuarioRepository = usuarioRepository;
        }

        @PostConstruct
        public void init() {
            SecurityUtils.setUsuarioRepository(usuarioRepository);
        }
    }
}