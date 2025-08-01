package com.deliverytech.delivery.controllers;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.entity.Restaurante;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @BeforeEach
    void setup() {
        restauranteRepository.deleteAll();

        Restaurante r1 = new Restaurante();
        r1.setNome("Sushi House");
        r1.setCategoria("Japonesa");
        r1.setEndereco("Av.Japonesa,123 - 01001-0000");
        r1.setTelefone("1190000-0000");
        r1.setTaxaEntrega(BigDecimal.valueOf(10.00));
        r1.setAtivo(true);

        Restaurante r2 = new Restaurante();
        r2.setNome("Pizza Bella");
        r2.setCategoria("Italiana");
        r2.setEndereco("Av.Japonesa,123 - 00000-0000");
        r2.setTelefone("1190000-0000");
        r2.setTaxaEntrega(BigDecimal.valueOf(5.00));
        r2.setAtivo(true);

        Restaurante r3 = new Restaurante();
        r3.setNome("Burger King");
        r3.setCategoria("Fast Food");
        r3.setEndereco("Av.Japonesa,123 - 00000-0000");
        r3.setTelefone("1190000-0000");
        r3.setTaxaEntrega(BigDecimal.valueOf(7.00));
        r3.setAtivo(false);

        restauranteRepository.saveAll(List.of(r1, r2, r3));
    }

    @Test
    void deveCadastrarRestauranteComSucesso() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Novo");
        request.setCategoria("Brasileira");
        request.setEndereco("Av. 01 - 00000-000");
        request.setTelefone("1190000-0000");
        request.setTaxaEntrega(BigDecimal.valueOf(8.50));
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("Restaurante Novo"))
            .andExpect(jsonPath("$.dados.categoria").value("Brasileira"));
    }

    @Test
    void deveBuscarRestaurantePorId() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);

        mockMvc.perform(get("/api/restaurantes/{id}", restaurante.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(restaurante.getNome()));
    }

    @Test
    void deveListarRestaurantesComFiltros() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                .param("categoria", "Japonesa")
                .param("ativo", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.length()").value(1))
            .andExpect(jsonPath("$.dados[0].categoria").value("Japonesa"))
            .andExpect(jsonPath("$.dados[0].ativo").value(true));
    }

    @Test
    void deveAtualizarRestaurante() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);

        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Sushi Master");
        request.setCategoria("Japonesa");
        request.setEndereco("Av.Japonesa,123 - 01001-0000");
        request.setTelefone("1190000-0000");
        request.setTaxaEntrega(BigDecimal.valueOf(12.00));
        request.setAtivo(true);

        mockMvc.perform(put("/api/restaurantes/{id}", restaurante.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value("Sushi Master"))
            .andExpect(jsonPath("$.dados.taxaEntrega").value(12.00));
    }

    @Test
    void deveAlterarStatusRestaurante() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);
        boolean statusInicial = restaurante.getAtivo();

        mockMvc.perform(patch("/api/restaurantes/{id}/status", restaurante.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.ativo").value(!statusInicial));
    }

    @Test
    void deveCalcularTaxaEntrega() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);

        mockMvc.perform(get("/api/restaurantes/{id}/taxa-entrega/{cep}", restaurante.getId(), "00001-000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").value(restaurante.getTaxaEntrega().doubleValue()));
    }

    @Test
    void deveListarRestaurantesProximos() throws Exception {
        mockMvc.perform(get("/api/restaurantes/proximos/{cep}", "01310930"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").isArray());
    }
}
