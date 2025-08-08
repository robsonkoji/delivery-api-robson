package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.AuthRequest;
import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.dto.response.AuthResponse;
import com.deliverytech.delivery.dto.response.UserResponse;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.mapper.UsuarioMapper;
import com.deliverytech.delivery.metrics.UsuarioAtivoMetrics;
import com.deliverytech.delivery.security.JwtTokenBlacklist;
import com.deliverytech.delivery.security.JwtUtil;
import com.deliverytech.delivery.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "Autenticação", description = "Operações para login, registro e informações do usuário autenticado")
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final UsuarioAtivoMetrics usuarioAtivoMetrics;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService,
                          UsuarioAtivoMetrics usuarioAtivoMetrics,
                          JwtTokenBlacklist jwtTokenBlacklist) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.usuarioAtivoMetrics = usuarioAtivoMetrics;
        this.jwtTokenBlacklist = jwtTokenBlacklist;
    }

    @Operation(summary = "Autentica usuário e gera token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticação bem sucedida"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String correlationId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Usuario usuario = (Usuario) authentication.getPrincipal();

            usuarioAtivoMetrics.registrarLogin(usuario.getId().toString());
            String token = jwtUtil.generateToken(usuario);

            AuthResponse response = new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getRole().name()
            );

            Map<String, Object> logData = new HashMap<>();
            logData.put("event", "USER_LOGIN");
            logData.put("correlationId", correlationId);
            logData.put("timestamp", timestamp.toString());
            logData.put("userId", usuario.getId());
            logData.put("email", usuario.getEmail());
            logData.put("status", "SUCCESS");

            logger.info("{}", logData);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, Object> logData = new HashMap<>();
            logData.put("event", "USER_LOGIN");
            logData.put("correlationId", correlationId);
            logData.put("timestamp", timestamp.toString());
            logData.put("email", request.getEmail());
            logData.put("status", "FAIL");
            logData.put("reason", "INVALID_CREDENTIALS");

            logger.warn("{}", logData);

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    @Operation(summary = "Registra um novo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        String correlationId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        if (usuarioService.existsByEmail(request.getEmail())) {
            logger.warn("{}", Map.of(
                "event", "USER_REGISTER",
                "correlationId", correlationId,
                "timestamp", timestamp.toString(),
                "email", request.getEmail(),
                "status", "FAIL",
                "reason", "EMAIL_ALREADY_EXISTS"
            ));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Usuario novoUsuario = usuarioService.registrarUsuario(request);
        UserResponse response = UsuarioMapper.toResponse(novoUsuario);

        logger.info("{}", Map.of(
            "event", "USER_REGISTER",
            "correlationId", correlationId,
            "timestamp", timestamp.toString(),
            "userId", novoUsuario.getId(),
            "email", novoUsuario.getEmail(),
            "status", "SUCCESS"
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtém informações do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Usuario usuario) {
        String correlationId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        if (usuario == null) {
            logger.warn("{}", Map.of(
                "event", "USER_ME",
                "correlationId", correlationId,
                "timestamp", timestamp.toString(),
                "status", "FAIL",
                "reason", "UNAUTHORIZED"
            ));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        logger.info("{}", Map.of(
            "event", "USER_ME",
            "correlationId", correlationId,
            "timestamp", timestamp.toString(),
            "userId", usuario.getId(),
            "email", usuario.getEmail(),
            "status", "SUCCESS"
        ));

        UserResponse response = UsuarioMapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout")
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       @AuthenticationPrincipal Usuario usuario) {
        String correlationId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();

        if (usuario != null) {
            usuarioAtivoMetrics.registrarLogout(usuario.getId().toString());
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Instant expiration = jwtUtil.getExpirationInstant(token);
            jwtTokenBlacklist.add(token, expiration);
        }

        logger.info("{}", Map.of(
            "event", "USER_LOGOUT",
            "correlationId", correlationId,
            "timestamp", timestamp.toString(),
            "userId", usuario != null ? usuario.getId() : null,
            "email", usuario != null ? usuario.getEmail() : null,
            "status", "SUCCESS"
        ));

        return ResponseEntity.noContent().build();
    }
}
