package com.deliverytech.delivery.controllers;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ClienteControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll();
        clienteRepository.saveAll(List.of(
            new Cliente(null, "João Silva", "joao@email.com", "11999999999", "Rua A", true),
            new Cliente(null, "Maria Oliveira", "maria@email.com", "11988888888", "Rua B", true),
            new Cliente(null, "João Souza", "souza@email.com", "11977777777", "Rua C", false)
        ));
    }

    // ✅ Criação bem-sucedida - Status 201
    @Test
    void deveCadastrarNovoClienteComSucesso() throws Exception {
        ClienteRequest request = new ClienteRequest("Carlos Pereira", "carlos@email.com", "11777777777", "Rua C");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("Carlos Pereira"));
    }

    // ✅ Conflito de dados - Status 409 (email já existente)
    @Test
    void deveRetornarConflitoQuandoEmailJaExiste() throws Exception {
        ClienteRequest request = new ClienteRequest("Outro Nome", "joao@email.com", "11900000000", "Rua Qualquer");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    // ✅ Dados inválidos - Status 400
    @Test
    void deveRetornarErroQuandoDadosInvalidos() throws Exception {
        ClienteRequest request = new ClienteRequest("", "", "", "");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // ✅ Busca existente - Status 200
    @Test
    void deveBuscarClientePorIdComSucesso() throws Exception {
        Cliente cliente = clienteRepository.findAll().get(0);

        mockMvc.perform(get("/api/clientes/{id}", cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(cliente.getNome()));
    }

    // ✅ Busca inexistente - Status 404
    @Test
    void deveRetornar404QuandoBuscarClienteInexistente() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", 9999L))
            .andExpect(status().isNotFound());
    }

    // ✅ Paginação - Metadados corretos
    @Test
    void deveRetornarPaginacaoComMetadadosCorretos() throws Exception {
        mockMvc.perform(get("/api/clientes/page")
                .param("page", "0")
                .param("size", "2")
                .param("sort", "nome,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.content", hasSize(2)))
            .andExpect(jsonPath("$.dados.totalElements", is(3)))
            .andExpect(jsonPath("$.dados.totalPages", is(2)))
            .andExpect(jsonPath("$.dados.number", is(0)));
    }

    // Demais testes adicionais já estavam corretos:
    @Test
    void deveBuscarClientePorEmail() throws Exception {
        mockMvc.perform(get("/api/clientes/email")
                .param("email", "joao@email.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.email").value("joao@email.com"));
    }

    @Test
    void deveListarClientesAtivos() throws Exception {
        mockMvc.perform(get("/api/clientes/ativos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados", hasSize(2)));
    }

    @Test
    void deveListarTodosClientes() throws Exception {
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados", hasSize(3)));
    }

    @Test
    void deveBuscarClientesPorNome() throws Exception {
        mockMvc.perform(get("/api/clientes/nome")
                .param("nome", "João"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados", hasSize(1)));
    }

    @Test
    void deveInativarCliente() throws Exception {
        Cliente cliente = clienteRepository.findAll().get(0);

        mockMvc.perform(patch("/api/clientes/{id}/inativar", cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.ativo").value(false));
    }

    @Test
    void deveReativarCliente() throws Exception {
        Cliente cliente = clienteRepository.findAll().get(2); // inativo

        mockMvc.perform(patch("/api/clientes/{id}/reativar", cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.ativo").value(true));
    }
}
