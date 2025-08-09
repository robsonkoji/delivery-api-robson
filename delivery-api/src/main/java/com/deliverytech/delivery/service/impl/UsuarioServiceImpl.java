package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.UsuarioRepository;
import com.deliverytech.delivery.service.UsuarioService;

import io.micrometer.core.instrument.MeterRegistry;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

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

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("delivery-api");

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
        Span span = tracer.spanBuilder("UsuarioServiceImpl.registrarUsuario").startSpan();
        span.setAttribute("email", request.getEmail());
        try {
            usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    String msg = "Email já está em uso: " + request.getEmail();
                    logger.warn("[{}] Falha ao registrar usuário: {}", getCorrelationId(), msg);
                    span.recordException(new IllegalArgumentException(msg));
                    span.setAttribute("error", true);
                    throw new IllegalArgumentException(msg);
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
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Span span = tracer.spanBuilder("UsuarioServiceImpl.existsByEmail").startSpan();
        span.setAttribute("email", email);
        try {
            boolean exists = usuarioRepository.existsByEmail(email);
            logger.debug("[{}] Verificando existência do email '{}': {}", getCorrelationId(), email, exists);
            return exists;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public Usuario getUsuarioLogado() {
        Span span = tracer.spanBuilder("UsuarioServiceImpl.getUsuarioLogado").startSpan();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                String msg = "Usuário não está autenticado";
                logger.error("[{}] Tentativa de obter usuário logado sem autenticação", getCorrelationId());
                span.recordException(new RuntimeException(msg));
                span.setAttribute("error", true);
                throw new RuntimeException(msg);
            }

            String email = authentication.getName();
            logger.debug("[{}] Obtendo usuário logado pelo email: {}", getCorrelationId(), email);
            span.setAttribute("email", email);

            return usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        String msg = "Usuário logado não encontrado";
                        logger.error("[{}] Usuário logado não encontrado no banco: {}", getCorrelationId(), email);
                        span.recordException(new EntityNotFoundException(msg));
                        span.setAttribute("error", true);
                        return new EntityNotFoundException(msg);
                    });
        } finally {
            span.end();
        }
    }
}
