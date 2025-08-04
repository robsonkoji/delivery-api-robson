package com.deliverytech.delivery.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.repository.*;
import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarPedidoComProdutosValidos() throws Exception {
        PedidoRequest pedido = new PedidoRequest();
        pedido.setClienteId(1L);
        pedido.setItens(List.of(new ItemPedidoRequest(1L, 2), new ItemPedidoRequest(2L, 1)));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").exists())
                .andExpect(jsonPath("$.status").value("RECEBIDO"));
    }

    @Test
    void deveRetornarErroParaProdutoInexistente() throws Exception {
        PedidoRequest pedido = new PedidoRequest();
        pedido.setClienteId(1L);
        pedido.setItens(List.of(new ItemPedidoRequest(999L, 1)));

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isNotFound());
    }

    // + Testar PUT de status
    // + Testar GET histórico por cliente
    // + Testar erro de estoque insuficiente
}
