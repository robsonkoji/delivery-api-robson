package com.deliverytech.delivery.entity;

import com.deliverytech.delivery.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidade que representa um usuário do sistema")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nome;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email do usuário (utilizado para login)", example = "joao@email.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Senha criptografada do usuário", example = "$2a$10$...")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Papel do usuário no sistema", example = "ADMIN")
    private Role role;

    @Column(nullable = false)
    @Schema(description = "Indica se a conta está ativa", example = "true")
    private Boolean ativo;

    @Column(nullable = false)
    @Schema(description = "Data de criação da conta", example = "2025-08-01T12:00:00")
    private LocalDateTime dataCriacao;

    @Column(name = "restaurante_id")
    @Schema(description = "ID do restaurante vinculado ao usuário, se aplicável", example = "2")
    private Long restauranteId;

    @PrePersist
    public void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true; // ou false, conforme seu padrão
        }
    }

    // Implementação do contrato UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(ativo);
    }
}
