package com.deliverytech.delivery.controllers;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.repository.UsuarioRepository;
import com.deliverytech.delivery.security.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // ambiente limpo a cada teste
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private JwtUtil jwtUtil;

    private String jwtToken;

    @BeforeEach
    void setup() {
        // Cria um usuário para autenticação
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário Teste");
        usuario.setEmail("usuario_teste@email.com");
        usuario.setSenha("123456"); // ideal hash na real, mas aqui só para teste
        usuario.setRole(Role.ADMIN);
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        usuarioRepository.save(usuario);

        // Gera token JWT para o usuário criado
        jwtToken = "Bearer " + jwtUtil.generateToken(usuario);
    }

    @Test
    void deveCriarClienteComDadosValidos() throws Exception {
        ClienteRequest request = new ClienteRequest("João Silva", "joao@email.com", "11900000000", "av.01 00000-000");

        mockMvc.perform(post("/api/clientes")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("João Silva"))
            .andExpect(jsonPath("$.dados.email").value("joao@email.com"));
    }

    @Test
    void deveRetornarErroAoCriarClienteComEmailInvalido() throws Exception {
        ClienteRequest request = new ClienteRequest();
        request.setNome("João Silva");
        request.setTelefone("11900000000");
        request.setEndereco("av.01 00000-000");
        request.setEmail("alan");


        mockMvc.perform(post("/api/clientes")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity());
    }
}
