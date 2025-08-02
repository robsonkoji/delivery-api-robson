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
        r2.setCategoria("italiana");
        r2.setEndereco("Av.Italiana,123 - 00000-0000");
        r2.setTelefone("1190000-0000");
        r2.setTaxaEntrega(BigDecimal.valueOf(5.00));
        r2.setAtivo(true);

        Restaurante r3 = new Restaurante();
        r3.setNome("Burger King");
        r3.setCategoria("fast-food");
        r3.setEndereco("Av.Fast,123 - 00000-0000");
        r3.setTelefone("1190000-0000");
        r3.setTaxaEntrega(BigDecimal.valueOf(7.00));
        r3.setAtivo(false);

        restauranteRepository.saveAll(List.of(r1, r2, r3));
    }

    // ✅ Criação bem-sucedida - Status 201
    @Test
    void deveCadastrarRestauranteComSucesso() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Novo");
        request.setCategoria("Brasileira");
        request.setEndereco("Av. 01 - 00000-000");
        request.setTelefone("11900000000");
        request.setHorarioFuncionamento("10:00-22:00");
        request.setTempoEntrega(10);
        request.setTaxaEntrega(BigDecimal.valueOf(8.50));
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.dados.nome").value("Restaurante Novo"))
            .andExpect(jsonPath("$.dados.categoria").value("Brasileira"));
    }

    // ✅ Busca existente - Status 200
    @Test
    void deveBuscarRestaurantePorId() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);

        mockMvc.perform(get("/api/restaurantes/{id}", restaurante.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.nome").value(restaurante.getNome()));
    }

    // ✅ Busca inexistente - Status 404
    @Test
    void deveRetornar404QuandoBuscarRestauranteInexistente() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", 9999L))
            .andExpect(status().isNotFound());
    }

    // ✅ Dados inválidos - Status 400
    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        RestauranteRequest request = new RestauranteRequest(); // campos nulos

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // ✅ Conflito de dados - Status 409 (nome duplicado)
    @Test
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        Restaurante restauranteExistente = restauranteRepository.findAll().get(0);

        RestauranteRequest request = new RestauranteRequest();
        request.setNome(restauranteExistente.getNome()); // mesmo nome
        request.setCategoria("Categoria");
        request.setEndereco("Endereco, cep 70000-010");
        request.setTelefone("11999999999");
        request.setTaxaEntrega(BigDecimal.valueOf(5.0));
        request.setTempoEntrega(10);
        request.setHorarioFuncionamento("10:00-12:00");

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    // ✅ Paginação - Metadados corretos
    @Test
    void deveRetornarRestaurantesPaginados() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                .param("page", "0")
                .param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.metadados.totalPaginas").exists())
            .andExpect(jsonPath("$.metadados.totalElementos").value(3))
            .andExpect(jsonPath("$.dados.length()").value(2));
    }

    // ✅ Listagem com filtros
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

    // ✅ Atualização
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

    // ✅ Alteração de status (PATCH)
    @Test
    void deveAlterarStatusRestaurante() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);
        boolean statusInicial = restaurante.getAtivo();

        mockMvc.perform(patch("/api/restaurantes/{id}/status", restaurante.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados.ativo").value(!statusInicial));
    }

    // ✅ Cálculo de taxa
    @Test
    void deveCalcularTaxaEntrega() throws Exception {
        Restaurante restaurante = restauranteRepository.findAll().get(0);

        mockMvc.perform(get("/api/restaurantes/{id}/taxa-entrega/{cep}", restaurante.getId(), "00001-000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").value(restaurante.getTaxaEntrega().doubleValue()));
    }

    // ✅ Restaurantes próximos (simulado)
    @Test
    void deveListarRestaurantesProximos() throws Exception {
        mockMvc.perform(get("/api/restaurantes/proximos/{cep}", "01310930"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dados").isArray());
    }
    
        // ❌ Cadastro com nome vazio
    @Test
    void deveRetornar400QuandoNomeVazio() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome(""); // inválido
        request.setCategoria("Japonesa");
        request.setEndereco("Av. 01 - 00000-000");
        request.setTelefone("1190000-0000");
        request.setTaxaEntrega(BigDecimal.valueOf(8.5));
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details").isArray());
    }

    // ❌ Cadastro com categoria inválida (não mapeada no enum)
    @Test
    void deveRetornar422QuandoCategoriaInvalida() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Nome válido");
        request.setCategoria("CategoriaNaoExistente");
        request.setEndereco("Av. Exemplo - 00000-000");
        request.setTelefone("1190000-0000");
        request.setTaxaEntrega(BigDecimal.valueOf(5.0));
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").value("Categoria inválida: CategoriaNaoExistente"));
    }

    // ❌ Cadastro com telefone em formato inválido
    @Test
    void deveRetornar400QuandoTelefoneInvalido() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Teste");
        request.setCategoria("Italiana");
        request.setEndereco("Endereço Exemplo");
        request.setTelefone("telefone-invalido"); // inválido
        request.setTaxaEntrega(BigDecimal.valueOf(10));
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details").isArray());
    }

    // ❌ Cadastro com taxa de entrega negativa
    @Test
    void deveRetornar400QuandoTaxaEntregaNegativa() throws Exception {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Teste");
        request.setCategoria("Brasileira");
        request.setEndereco("Endereço Exemplo");
        request.setTelefone("1190000-0000");
        request.setTaxaEntrega(BigDecimal.valueOf(-5.0)); // inválida
        request.setAtivo(true);

        mockMvc.perform(post("/api/restaurantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.details").isArray());
    }
}