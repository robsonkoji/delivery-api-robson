package com.deliverytech.delivery.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    private Produto produtoExistente;
    private Restaurante restauranteExistente;

    @BeforeEach
    void setup() {
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();

        Restaurante restaurante1 = new Restaurante();
        restaurante1.setNome("Restaurante Exemplo");
        restaurante1.setCategoria("Restaurante");
        restaurante1.setTaxaEntrega(new BigDecimal(5.00));
        restaurante1.setEndereco("Rua das Laranjeiras, 456");
        restaurante1.setTelefone("11999999999");
        

        restauranteExistente = restauranteRepository.save(restaurante1);
       

        Produto produto1 = new Produto();
        produto1.setNome("X-Burguer");
        produto1.setDescricao("Hambúrguer simples");
        produto1.setPreco(new BigDecimal("12.50"));
        produto1.setCategoria("Lanches");
        produto1.setDisponivel(true);
        produto1.setRestaurante(restaurante1);

        Produto produto2 = new Produto();
        produto2.setNome("Refrigerante");
        produto2.setDescricao("Coca-Cola 350ml");
        produto2.setPreco(new BigDecimal("5.00"));
        produto2.setCategoria("Bebidas");
        produto2.setDisponivel(true);
        produto2.setRestaurante(restaurante1);

        produtoRepository.saveAll(List.of(produto1, produto2));
        
    }


    @Test
    void deveCadastrarProdutoComSucesso() throws Exception {

        ProdutoRequest request = new ProdutoRequest();
        request.setNome("X-Salada");
        request.setDescricao("Hambúrguer com salada");
        request.setPreco(new BigDecimal(18.50));
        request.setDisponivel(true);
        request.setCategoria("Lanches");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("X-Salada"))
            .andExpect(jsonPath("$.dados.disponivel").value(true));
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        produtoExistente = produtoRepository.findAll().get(0);
        mockMvc.perform(get("/api/produtos/{id}", produtoExistente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(produtoExistente.getNome()));
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws Exception {
        produtoExistente = produtoRepository.findAll().get(0);

        ProdutoRequest request = new ProdutoRequest();
        request.setNome("X-Burguer Atualizado");
        request.setDescricao("Hambúrguer atualizado");
        request.setPreco(new BigDecimal("20.00"));
        request.setDisponivel(true);
        request.setCategoria("Lanches");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(put("/api/produtos/{id}", produtoExistente.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value("X-Burguer Atualizado"));
    }

    @Test
    void deveRemoverProdutoComSucesso() throws Exception {
        produtoExistente = produtoRepository.findAll().get(0);
        
        mockMvc.perform(delete("/api/produtos/{id}", produtoExistente.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveAlterarDisponibilidadeDoProduto() throws Exception {
        produtoExistente = produtoRepository.findAll().get(0);

        mockMvc.perform(patch("/api/produtos/{id}/disponibilidade", produtoExistente.getId())
                .param("disponivel", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.disponivel").value(false));
    }

    @Test
    void deveListarProdutosPorCategoria() throws Exception {
        mockMvc.perform(get("/api/produtos/categoria/{categoria}", "Lanches"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").isArray())
            .andExpect(jsonPath("$.dados[0].categoria").value("Lanches"));
    }

    @Test
    void deveBuscarProdutosPorNome() throws Exception {
        mockMvc.perform(get("/api/produtos/buscar")
                .param("nome", "X-Burguer"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados[0].nome").value("X-Burguer"));
    }

    @Test
    void deveListarProdutosPorRestaurante() throws Exception {
        produtoExistente = produtoRepository.findAll().get(0);
        restauranteExistente = restauranteRepository.findAll().get(0);

        mockMvc.perform(get("/api/produtos/restaurantes/{restauranteId}", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").isArray())
            .andExpect(jsonPath("$.dados[0].restauranteId").value(1));
    }
}
