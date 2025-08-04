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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    private Restaurante restauranteExistente;
    private Produto produtoExistente;

    @BeforeEach
    void setup() {
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();

        restauranteExistente = new Restaurante();
        restauranteExistente.setNome("Restaurante Exemplo");
        restauranteExistente.setCategoria("brasileira");
        restauranteExistente.setTaxaEntrega(new BigDecimal("5.00"));
        restauranteExistente.setEndereco("Rua das Laranjeiras, 456");
        restauranteExistente.setTelefone("11999999999");
        restauranteExistente = restauranteRepository.save(restauranteExistente);

        produtoExistente = new Produto();
        produtoExistente.setNome("X-Burguer");
        produtoExistente.setDescricao("Hambúrguer simples");
        produtoExistente.setPreco(new BigDecimal("12.50"));
        produtoExistente.setCategoria("lanche");
        produtoExistente.setDisponivel(true);
        produtoExistente.setRestaurante(restauranteExistente);
        produtoExistente = produtoRepository.save(produtoExistente);
    }

    @Test
    void deveCadastrarProdutoComSucesso() throws Exception {
        ProdutoRequest request = new ProdutoRequest();
        request.setNome("X-Salada");
        request.setDescricao("Com salada");
        request.setPreco(new BigDecimal("18.50"));
        request.setDisponivel(true);
        request.setCategoria("lanche");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("X-Salada"));
    }

    @Test
    void deveFalharAoCadastrarProdutoComDadosInvalidos() throws Exception {
        ProdutoRequest request = new ProdutoRequest(); // vazio

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarConflitoAoCadastrarProdutoComMesmoNomeERestaurante() throws Exception {
        ProdutoRequest request = new ProdutoRequest();
        request.setNome(produtoExistente.getNome());
        request.setDescricao("Duplicado");
        request.setPreco(new BigDecimal("15.00"));
        request.setDisponivel(true);
        request.setCategoria("lanche");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", produtoExistente.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(produtoExistente.getNome()));
    }

    @Test
    void deveRetornar404AoBuscarProdutoInexistente() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", 999L))
            .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws Exception {
        ProdutoRequest request = new ProdutoRequest();
        request.setNome("X-Burguer Atualizado");
        request.setDescricao("Atualizado");
        request.setPreco(new BigDecimal("20.00"));
        request.setDisponivel(true);
        request.setCategoria("lanche");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(put("/api/produtos/{id}", produtoExistente.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value("X-Burguer Atualizado"))
            .andExpect(jsonPath("$.dados.disponivel").value(true));
    }

    @Test
    void deveRemoverProdutoComSucesso() throws Exception {
        mockMvc.perform(delete("/api/produtos/{id}", produtoExistente.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    void deveAlterarDisponibilidadeDoProduto() throws Exception {
        mockMvc.perform(patch("/api/produtos/{id}/disponibilidade", produtoExistente.getId())
                .param("disponivel", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.disponivel").value(false));
    }

    @Test
    void deveListarProdutosPaginadosComMetadados() throws Exception {
        mockMvc.perform(get("/api/produtos")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.totalElements").exists())
            .andExpect(jsonPath("$.meta.size").value(10));
    }

    @Test
    void deveRetornarErro422AoCadastrarProdutoComPrecoNegativo() throws Exception {
        ProdutoRequest request = new ProdutoRequest();
        request.setNome("Produto Inválido");
        request.setDescricao("Preço negativo");
        request.setPreco(new BigDecimal("-5.00")); // <- preço negativo
        request.setDisponivel(true);
        request.setCategoria("lanche");
        request.setRestauranteId(restauranteExistente.getId());

        mockMvc.perform(post("/api/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").value("Violação de validação de campos"))
            .andExpect(jsonPath("$.details[0]").value("preco: Preço deve ser maior que zero"));
    }
}
