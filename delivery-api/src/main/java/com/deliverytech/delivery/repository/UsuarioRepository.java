package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca usuário pelo email (login).
     * Retorna Optional para tratar caso não encontre.
     */
    Optional<Usuario> findByEmail(String email);
}
