package com.deliverytech.delivery.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.deliverytech.delivery.enums.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Senha deve ser armazenada com hash BCrypt.
     */
    @Column(nullable = false)
    private String senha;

    /**
     * Papel do usuário, conforme enum Role.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Usuário ativo/inativo para controle de acesso.
     */
    @Column(nullable = false)
    private Boolean ativo;

    /**
     * Data/hora de criação do usuário.
     */
    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Id do restaurante, apenas preenchido para usuários com role RESTAURANTE.
     */
    @Column(nullable = true)
    private Long restauranteId;

    /**
     * Retorna as autoridades do usuário, no formato ROLE_<NOME_ROLE>.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    /**
     * Retorna a senha do usuário para autenticação.
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o username (email) para autenticação.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica se a conta não expirou.
     * Aqui sempre true, pode implementar lógica customizada.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica se a conta não está bloqueada.
     * Aqui sempre true, pode implementar lógica customizada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais não expiraram.
     * Aqui sempre true, pode implementar lógica customizada.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está ativo (habilitado).
     */
    @Override
    public boolean isEnabled() {
        return ativo != null && ativo;
    }
}
