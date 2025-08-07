package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.UsuarioRepository;
import com.deliverytech.delivery.service.UsuarioService;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    // Gauge: número de usuários ativos
    private final AtomicInteger usuariosAtivos = new AtomicInteger();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              MeterRegistry meterRegistry) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }

    // Registra a métrica do Gauge após construção do bean
    @PostConstruct
    public void init() {
        meterRegistry.gauge("usuarios_ativos", usuariosAtivos);
    }

    // Método de exemplo: poderia ser chamado quando o usuário fizer login
    public void usuarioLogado() {
        usuariosAtivos.incrementAndGet();
    }

    // Método de exemplo: poderia ser chamado quando o usuário fizer logout
    public void usuarioDeslogado() {
        usuariosAtivos.decrementAndGet();
    }

    @Override
    public Usuario registrarUsuario(RegisterRequest request) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(request.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole());
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        if (request.getRole() == Role.RESTAURANTE) {
            usuario.setRestauranteId(request.getRestauranteId());
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não está autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado"));
    }
}
