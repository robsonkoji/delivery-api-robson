package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.UsuarioRepository;
import com.deliverytech.delivery.service.UsuarioService;

import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeterRegistry meterRegistry;

    private final AtomicInteger usuariosAtivos = new AtomicInteger(0);

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              MeterRegistry meterRegistry) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.meterRegistry = meterRegistry;
    }

    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    @PostConstruct
    public void init() {
        meterRegistry.gauge("usuarios_ativos", usuariosAtivos);
        logger.info("[{}] Gauge 'usuarios_ativos' registrado com sucesso.", getCorrelationId());
    }

    public void usuarioLogado() {
        int total = usuariosAtivos.incrementAndGet();
        logger.info("[{}] Usuário logado. Total de usuários ativos: {}", getCorrelationId(), total);
    }

    public void usuarioDeslogado() {
        int total = usuariosAtivos.decrementAndGet();
        logger.info("[{}] Usuário deslogado. Total de usuários ativos: {}", getCorrelationId(), total);
    }

    @Override
    public Usuario registrarUsuario(RegisterRequest request) {
        logger.info("[{}] Tentando registrar usuário com email: {}", getCorrelationId(), request.getEmail());

        usuarioRepository.findByEmail(request.getEmail())
            .ifPresent(u -> {
                logger.warn("[{}] Falha ao registrar usuário: email já está em uso: {}", getCorrelationId(), request.getEmail());
                throw new IllegalArgumentException("Email já está em uso.");
            });

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

        Usuario salvo = usuarioRepository.save(usuario);
        logger.info("[{}] Usuário registrado com sucesso: id={}, email={}", getCorrelationId(), salvo.getId(), salvo.getEmail());

        return salvo;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = usuarioRepository.existsByEmail(email);
        logger.debug("[{}] Verificando existência do email '{}': {}", getCorrelationId(), email, exists);
        return exists;
    }

    @Override
    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("[{}] Tentativa de obter usuário logado sem autenticação", getCorrelationId());
            throw new RuntimeException("Usuário não está autenticado");
        }

        String email = authentication.getName();
        logger.debug("[{}] Obtendo usuário logado pelo email: {}", getCorrelationId(), email);

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("[{}] Usuário logado não encontrado no banco: {}", getCorrelationId(), email);
                    return new EntityNotFoundException("Usuário logado não encontrado");
                });
    }
}
