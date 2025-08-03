package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.AuthRequest;
import com.deliverytech.delivery.dto.request.RegisterRequest;
import com.deliverytech.delivery.dto.response.AuthResponse;
import com.deliverytech.delivery.dto.response.UserResponse;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.security.JwtUtil;
import com.deliverytech.delivery.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Operações para login, registro e informações do usuário autenticado")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Autentica usuário e gera token JWT",
        description = "Realiza autenticação usando email e senha, retornando um token JWT para uso nas chamadas autenticadas.",
        requestBody = @RequestBody(
            description = "Credenciais para autenticação",
            required = true,
            content = @Content(schema = @Schema(implementation = AuthRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem sucedida",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
        }
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Usuario usuario = (Usuario) authentication.getPrincipal();

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

    @PostMapping("/register")
    @Operation(
        summary = "Registra um novo usuário",
        description = "Cria um novo usuário com os dados fornecidos, se o email ainda não estiver cadastrado.",
        requestBody = @RequestBody(
            description = "Dados para registro de usuário",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email já cadastrado", content = @Content)
        }
    )
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioService.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        Usuario novoUsuario = usuarioService.registrarUsuario(request);

        UserResponse response = new UserResponse(
                novoUsuario.getId(),
                novoUsuario.getNome(),
                novoUsuario.getEmail(),
                novoUsuario.getRole().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Obtém informações do usuário autenticado",
        description = "Retorna dados básicos do usuário autenticado pelo token JWT.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
        },
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @SecurityRequirement(name = "bearerAuth")  // Para aplicar segurança no Swagger UI
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        UserResponse response = new UserResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}
