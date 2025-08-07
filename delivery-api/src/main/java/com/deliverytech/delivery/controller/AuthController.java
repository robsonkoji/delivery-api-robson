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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.time.Instant;

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


    @Operation(
        summary = "Autentica usuário e gera token JWT",
        description = "Realiza autenticação usando email e senha, retornando um token JWT para uso nas chamadas autenticadas.")
        @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem sucedida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Usuario usuario = (Usuario) authentication.getPrincipal();

            // MÉTRICA: adiciona usuário ativo
            usuarioAtivoMetrics.registrarLogin(usuario.getId().toString());

            String token = jwtUtil.generateToken(usuario);

            AuthResponse response = new AuthResponse(
                    token,
                    usuario.getEmail(),
                    usuario.getNome(),
                    usuario.getRole().name()
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
    }

    @Operation(
        summary = "Registra um novo usuário",
        description = "Cria um novo usuário com os dados fornecidos, se o email ainda não estiver cadastrado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Email já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioService.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Usuario novoUsuario = usuarioService.registrarUsuario(request);

        UserResponse response = UsuarioMapper.toResponse(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Obtém informações do usuário autenticado",
        description = "Retorna dados básicos do usuário autenticado pelo token JWT.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
        })
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        UserResponse response = UsuarioMapper.toResponse(usuario);


        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Logout",
        description = "Invalida a sessão do usuário (remove da métrica de usuários ativos e coloca o token na blacklist)"
    )
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       @AuthenticationPrincipal Usuario usuario) {
        if (usuario != null) {
            usuarioAtivoMetrics.registrarLogout(usuario.getId().toString());
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Instant expiration = jwtUtil.getExpirationInstant(token);
            jwtTokenBlacklist.add(token, expiration);
        }

        return ResponseEntity.noContent().build();
    }

}
