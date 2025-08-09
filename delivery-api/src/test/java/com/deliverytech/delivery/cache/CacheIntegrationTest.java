package com.deliverytech.delivery.cache;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.service.RestauranteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")  // Define perfil "test" para configurações específicas de teste
class CacheIntegrationTest {

    @Autowired
    private RestauranteService restauranteService;  // Serviço a ser testado

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;  // Template genérico para manipular Redis com objetos

    @Autowired
    private StringRedisTemplate stringRedisTemplate;  // Template para manipular Redis com strings

    @BeforeEach
    @SuppressWarnings("deprecation")
    void setup() {
        // Antes de cada teste, limpa o banco Redis para garantir ambiente limpo
        var connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory(), "RedisConnectionFactory não pode ser nulo");
        var connection = connectionFactory.getConnection();
        connection.flushDb();
    }

    @Test
    void deveArmazenarNoCacheAposPrimeiraChamada() {
        // Prepara objeto RestauranteRequest com dados para cadastro
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Teste");
        request.setCategoria("Italiana");
        request.setTaxaEntrega(new BigDecimal("10.00"));
        request.setTelefone("11999999999");
        request.setEndereco("Rua das Flores, 123 00000-000");

        // Cadastra o restaurante: operação deve salvar no banco e atualizar o cache
        RestauranteResponse cadastrado = restauranteService.cadastrarRestaurante(request);

        // Primeira busca pelo restaurante por ID: deve buscar no banco e armazenar no cache
        RestauranteResponse r1 = restauranteService.buscarRestaurantePorId(cadastrado.getId());

        // Segunda busca pelo mesmo restaurante: deve obter diretamente do cache
        RestauranteResponse r2 = restauranteService.buscarRestaurantePorId(cadastrado.getId());

        // Verifica se os dois objetos retornados são iguais em termos de conteúdo (igualdade lógica)
        assertThat(r1).isEqualTo(r2);

        // Consulta as chaves do Redis que contenham o prefixo 'restaurantes'
        Set<String> keys = stringRedisTemplate.keys("*restaurantes*");

        System.out.println("Chaves no Redis: " + keys);

        // Asserção para garantir que pelo menos uma chave de restaurante está no cache Redis
        assertThat(keys)
            .as("Verificar que existe pelo menos uma chave referente a restaurantes no Redis")
            .isNotEmpty();
    }
}
