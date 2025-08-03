package com.deliverytech.delivery.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.deliverytech.delivery.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email); // já deve existir

    boolean existsByEmail(String email); // <-- adicione esta linha
}