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
@ActiveProfiles("test")
class CacheIntegrationTest {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    @SuppressWarnings("deprecation")
    void setup() {
        // Garante que a ConnectionFactory não seja null
        var connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory(), "RedisConnectionFactory não pode ser nulo");
        var connection = connectionFactory.getConnection();
        connection.flushDb();
    }

    @Test
    void deveArmazenarNoCacheAposPrimeiraChamada() {
        // Cria um restaurante para testar cache
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Teste");
        request.setCategoria("Italiana");
        request.setTaxaEntrega(new BigDecimal("10.00"));
        request.setTelefone("11999999999");
        request.setEndereco("Rua das Flores, 123 00000-000");

        // Cadastra o restaurante (persistência + cache)
        RestauranteResponse cadastrado = restauranteService.cadastrarRestaurante(request);

        // Primeira chamada — busca no banco e popula cache
        RestauranteResponse r1 = restauranteService.buscarRestaurantePorId(cadastrado.getId());

        // Segunda chamada — busca no cache
        RestauranteResponse r2 = restauranteService.buscarRestaurantePorId(cadastrado.getId());

        // Validar que ambas chamadas retornam o mesmo objeto (igualdade lógica)
        assertThat(r1).isEqualTo(r2);

        // Verificar as chaves armazenadas no Redis com prefixo correto
        Set<String> keys = stringRedisTemplate.keys("*restaurantes*");

        System.out.println("Chaves no Redis: " + keys);
        assertThat(keys)
            .as("Verificar que existe pelo menos uma chave referente a restaurantes no Redis")
            .isNotEmpty();
    }
}
