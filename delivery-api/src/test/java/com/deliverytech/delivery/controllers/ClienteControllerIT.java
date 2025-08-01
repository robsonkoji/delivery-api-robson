package com.deliverytech.delivery.controllers;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
@ActiveProfiles("test")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll();

        Cliente cliente1 = new Cliente(null, "João Silva", "joao@email.com", "11999999999", "Rua A", true);
        Cliente cliente2 = new Cliente(null, "Maria Oliveira", "maria@email.com", "11988888888", "Rua B", true);
        Cliente cliente3 = new Cliente(null, "João Souza", "souza@email.com", "11977777777", "Rua C", false);

        clienteRepository.saveAll(List.of(cliente1, cliente2, cliente3));
    }

    @Test
    void deveCadastrarNovoClienteComSucesso() throws Exception {
        ClienteRequest request = new ClienteRequest("Carlos Pereira", "carlos@email.com", "11777777777", "Rua C");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("Carlos Pereira"));
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        Cliente cliente = clienteRepository.findAll().get(0);

        mockMvc.perform(get("/api/clientes/{id}", cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(cliente.getNome()));
    }

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
        Cliente cliente = clienteRepository.findAll().get(2); // já inativo

        mockMvc.perform(patch("/api/clientes/{id}/reativar", cliente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.ativo").value(true));
    }
}
