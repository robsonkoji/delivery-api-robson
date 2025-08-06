package com.deliverytech.delivery.controllers;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.repository.*;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;




@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PedidoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto1;
    private Produto produto2;
    private String token;

   @BeforeEach
void setup() throws Exception {
    String registerPayload = """
        {
            "nome": "Robson",
            "email": "robson@email.com",
            "senha": "123456",
            "role": "CLIENTE"
        }
    """;

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(registerPayload))
            .andExpect(status().isCreated()); // <-- Corrigido aqui

    // Login permanece esperando 200 OK
    String loginPayload = """
        {
            "email": "robson@email.com",
            "senha": "123456"
        }
    """;

    String response = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    token = objectMapper.readTree(response).get("token").asText();


        // Recupera o usuário criado do banco
        Usuario usuarioCliente = usuarioRepository.findByEmail("robson@email.com").orElseThrow();

        // Cria cliente associado
        cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setEndereco("Av. 01 00000000");
        cliente.setTelefone("11000000000");
        cliente.setUsuario(usuarioCliente);
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        // Cria usuário restaurante
        Usuario usuarioRestaurante = new Usuario();
        usuarioRestaurante.setNome("Restaurante Dono");
        usuarioRestaurante.setEmail("restaurante@teste.com");
        usuarioRestaurante.setSenha("123456");
        usuarioRestaurante.setRole(Role.RESTAURANTE);
        usuarioRestaurante.setDataCriacao(LocalDateTime.now());
        usuarioRestaurante.setAtivo(true);
        usuarioRestaurante = usuarioRepository.save(usuarioRestaurante);

        // Cria restaurante
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Teste");
        restaurante.setAtivo(true);
        restaurante.setUsuario(usuarioRestaurante);
        restaurante.setTaxaEntrega(new BigDecimal("7.50"));
        restaurante.setEndereco("Rua das Palmeiras, 123");
        restaurante.setTelefone("11999999999");
        restaurante.setCategoria("Italiana");
        restaurante = restauranteRepository.save(restaurante);

        //oduto1 = produtoRepository.save(new Produto("Produto 1", "Descrição do Produto 1", "Categoria Exemplo", new BigDecimal("10.00"), 1
        produto1 = new Produto();
        produto1.setNome("Produto 1");
        produto1.setDescricao("Descrição do Produto 1"); // campo obrigatório
        produto1.setCategoria("Categoria Exemplo");     // campo obrigatório
        produto1.setPreco(new BigDecimal("10.00"));
        produto1.setEstoque(10);
        produto1.setDisponivel(true);
        produto1.setRestaurante(restaurante);
        produto1 = produtoRepository.save(produto1);

        produto2 = new Produto();
        produto2.setNome("Produto 2");
        produto2.setDescricao("Descrição do Produto 2"); // campo obrigatório
        produto2.setCategoria("Categoria Exemplo");     // campo obrigatório
        produto2.setPreco(new BigDecimal("15.00"));
        produto2.setEstoque(5);
        produto2.setDisponivel(true);
        produto2.setRestaurante(restaurante);
        produto2 = produtoRepository.save(produto2);
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setRestauranteId(restaurante.getId());
        pedidoRequest.setEnderecoEntrega("Rua Teste, 123");
        pedidoRequest.setItens(List.of(
                new ItemPedidoRequest(produto1.getId(), 2),
                new ItemPedidoRequest(produto2.getId(), 1)
        ));

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.status", is(StatusPedido.CRIADO.name())))
            .andExpect(jsonPath("$.dados.cliente.id", is(cliente.getId().intValue())))
            .andExpect(jsonPath("$.dados.restauranteId", is(restaurante.getId().intValue())))
            .andExpect(jsonPath("$.dados.itens", hasSize(2)))
            .andExpect(jsonPath("$.dados.total", notNullValue()))
            .andDo(print());
    }

    @Test
    void deveRetornarErroProdutoInexistente() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setRestauranteId(restaurante.getId());
        pedidoRequest.setEnderecoEntrega("Rua Teste, 123 - 00000000");
        pedidoRequest.setItens(List.of(
                new ItemPedidoRequest(9999L, 1)
        ));

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
void deveAtualizarStatusPedido() throws Exception {
    PedidoRequest pedidoRequest = new PedidoRequest();
    pedidoRequest.setClienteId(cliente.getId());
    pedidoRequest.setRestauranteId(restaurante.getId());
    pedidoRequest.setEnderecoEntrega("Rua Teste, 123");
    pedidoRequest.setItens(List.of(new ItemPedidoRequest(produto1.getId(), 1)));

    String jsonResponse = mockMvc.perform(post("/api/pedidos")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pedidoRequest)))
        .andExpect(status().isCreated()) // criação retorna 201 Created
        .andReturn()
        .getResponse()
        .getContentAsString();

     // debug opcional
    System.out.println("### JSON response: " + jsonResponse);

    // extrai o ID do pedido dentro de "dados"
    Long pedidoId = objectMapper.readTree(jsonResponse)
            .path("dados")
            .path("id")
            .asLong();
    if (pedidoId == 0) {
        throw new IllegalStateException("ID do pedido retornou 0. JSON inválido: " + jsonResponse);
    }

    mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
            .header("Authorization", "Bearer " + token)
            .param("status", "PREPARANDO")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // atualização deve retornar 200 OK
        .andExpect(jsonPath("$.dados.status").value("PREPARANDO"))
        .andDo(print());
}

    @Test
    void deveBuscarPedidosPorCliente() throws Exception {
        PedidoRequest pedidoRequest = new PedidoRequest();
        pedidoRequest.setClienteId(cliente.getId());
        pedidoRequest.setRestauranteId(restaurante.getId());
        pedidoRequest.setEnderecoEntrega("Rua Teste, 123");
        pedidoRequest.setItens(List.of(new ItemPedidoRequest(produto1.getId(), 1)));

        mockMvc.perform(post("/api/pedidos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoRequest)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", cliente.getId())
                .header("Authorization", "Bearer " + token))
                .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados[0]", not(empty())))
            .andExpect(jsonPath("$.dados[0].cliente.id", is(cliente.getId().intValue())))
            .andExpect(jsonPath("$.dados[0].itens", not(empty())));
    }
}
