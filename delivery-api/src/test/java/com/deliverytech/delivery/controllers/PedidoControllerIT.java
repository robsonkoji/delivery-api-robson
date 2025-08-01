package com.deliverytech.delivery.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.repository.*;
import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@ActiveProfiles("test")
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto1;
    private Produto produto2;

    @BeforeEach
    void setup() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Fulano");
        cliente.setEmail("fulano@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua X");
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Bom");
        restaurante.setEndereco("Av. Principal");
        restaurante.setTelefone("11988888888");
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        produto1 = new Produto();
        produto1.setNome("Pizza");
        produto1.setPreco(BigDecimal.valueOf(30.00));
        produto1.setDisponivel(true);
        produto1.setRestaurante(restaurante);
        produto1 = produtoRepository.save(produto1);

        produto2 = new Produto();
        produto2.setNome("Refrigerante");
        produto2.setPreco(BigDecimal.valueOf(8.00));
        produto2.setDisponivel(true);
        produto2.setRestaurante(restaurante);
        produto2 = produtoRepository.save(produto2);
    }

    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(restaurante.getId());
        request.setEnderecoEntrega("Rua das Flores, 123");

        ItemPedidoRequest item1 = new ItemPedidoRequest(produto1.getId(), 1);
        ItemPedidoRequest item2 = new ItemPedidoRequest(produto2.getId(), 2);
        request.setItens(Arrays.asList(item1, item2));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andDo(print()) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.total", is(46.00)));
    }

    @Test
    void deveRetornar404ParaPedidoInexistente() throws Exception {
        mockMvc.perform(get("/api/pedidos/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarPedidoPorId() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setValorTotal(BigDecimal.valueOf(46.00));
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/" + pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.id").value(pedido.getId()));
    }

    @Test
    void deveFiltrarPedidosPorStatusEData() throws Exception {
        // Criar os itens manualmente
        ItemPedido item1 = new ItemPedido();
        item1.setProduto(produto1);
        item1.setQuantidade(2);
        item1.setPrecoUnitario(produto1.getPreco());
        item1.setSubtotal(produto1.getPreco().multiply(BigDecimal.valueOf(2)));

        ItemPedido item2 = new ItemPedido();
        item2.setProduto(produto2);
        item2.setQuantidade(3);
        item2.setPrecoUnitario(produto2.getPreco());
        item2.setSubtotal(produto2.getPreco().multiply(BigDecimal.valueOf(3)));

        // Criar pedido e associar os itens
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEnderecoEntrega("Rua Teste");
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setTaxaEntrega(BigDecimal.valueOf(5.0));
        pedido.setValorTotal(item1.getSubtotal().add(item2.getSubtotal()).add(BigDecimal.valueOf(5.0)));
        pedido.setItens(List.of(item1, item2));

        // associar os itens ao pedido (relacionamento bidirecional)
        item1.setPedido(pedido);
        item2.setPedido(pedido);

        pedidoRepository.save(pedido);

        String dataInicio = "30-07-2025 00:00";
        String dataFim = "31-07-2025 23:59";

        mockMvc.perform(get("/api/pedidos")
                .param("status", "CRIADO")
                .param("dataInicio", dataInicio)
                .param("dataFim", dataFim))
            .andExpect(status().isOk());
    }

    @Test
    void deveRetornar409ParaConflitoDeDados() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(restaurante.getId());
        request.setEnderecoEntrega("Rua das Flores");
        request.setItens(List.of(new ItemPedidoRequest(produto1.getId(), 1)));

        // Primeiro pedido
        mockMvc.perform(post("/api/pedidos")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Segundo pedido idêntico (simulação de duplicação lógica)
        mockMvc.perform(post("/api/pedidos")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }


    @Test
    void deveAtualizarStatusDoPedido() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setValorTotal(BigDecimal.valueOf(40));
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(patch("/api/pedidos/" + pedido.getId() + "/status")
                        .param("status", "ENTREGUE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados.status").value("ENTREGUE"));
    }

    @Test
    void deveCalcularTotalSemSalvar() throws Exception {
        List<ItemPedidoRequest> itens = Arrays.asList(
                new ItemPedidoRequest(produto1.getId(), 2),
                new ItemPedidoRequest(produto2.getId(), 3)
        );

        mockMvc.perform(post("/api/pedidos/calcular")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itens)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados", is(84.0)));
    }

    @Test
    void deveBuscarPedidosPorCliente() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setValorTotal(BigDecimal.TEN);
        pedidoRepository.save(pedido);

        mockMvc.perform(get("/api/pedidos/clientes/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dados[0].cliente").exists());
    }

    @Test
    void deveCancelarPedido() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setValorTotal(BigDecimal.valueOf(30));
        pedido = pedidoRepository.save(pedido);

        mockMvc.perform(delete("/api/pedidos/" + pedido.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarPedidosPaginadosComMetadados() throws Exception {
        for (int i = 0; i < 5; i++) {
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setEnderecoEntrega("Rua " + i);
            pedido.setStatus(StatusPedido.CRIADO);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setTaxaEntrega(BigDecimal.valueOf(5.0));
            pedido.setValorTotal(BigDecimal.valueOf(20.0 + i));
            pedidoRepository.save(pedido);
        }

        mockMvc.perform(get("/api/pedidos")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagina.totalPaginas").exists())
                .andExpect(jsonPath("$.pagina.totalElementos").value(5))
                .andExpect(jsonPath("$.pagina.tamanhoPagina").value(3))
                .andExpect(jsonPath("$.pagina.paginaAtual").value(0));
    }
}
