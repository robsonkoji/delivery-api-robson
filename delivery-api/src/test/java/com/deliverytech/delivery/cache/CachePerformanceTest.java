package com.deliverytech.delivery.cache;

import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.service.RestauranteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CachePerformanceTest {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setup() {
        // Limpa cache antes do teste
        if (redisTemplate != null) {
            var keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }

        // Usuário fake para autenticação
        Usuario usuarioFake = new Usuario();
        usuarioFake.setId(1L);
        usuarioFake.setEmail("admin@email.com");

        var authentication = new UsernamePasswordAuthenticationToken(
                usuarioFake,
                null,
                usuarioFake.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve retornar o mesmo resultado ao buscar do cache")
    void deveRetornarMesmoResultadoDoCache() {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Cache");
        request.setCategoria("Italiana");
        request.setTaxaEntrega(new BigDecimal("12.00"));
        request.setTelefone("11999998888");
        request.setEndereco("Rua Teste, 123");

        RestauranteResponse cadastrado = restauranteService.cadastrarRestaurante(request);

        RestauranteResponse primeiraChamada = restauranteService.buscarRestaurantePorId(cadastrado.getId());
        RestauranteResponse segundaChamada = restauranteService.buscarRestaurantePorId(cadastrado.getId());

        // Apenas validamos que o cache retorna o mesmo valor
        assertThat(segundaChamada).isEqualTo(primeiraChamada);
    }

    @Test
    @Disabled("Teste de performance - execute manualmente em ambiente controlado")
    @DisplayName("Benchmark: Cache deve ser mais rápido que a busca no DB")
    void deveDemonstrarGanhoDePerformanceComCache() {
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Performance");
        request.setCategoria("Italiana");
        request.setTaxaEntrega(new BigDecimal("12.00"));
        request.setTelefone("11999998888");
        request.setEndereco("Rua Teste, 123");

        RestauranteResponse cadastrado = restauranteService.cadastrarRestaurante(request);

        long startFirstCall = System.currentTimeMillis();
        restauranteService.buscarRestaurantePorId(cadastrado.getId());
        long durationFirstCall = System.currentTimeMillis() - startFirstCall;

        long startSecondCall = System.currentTimeMillis();
        restauranteService.buscarRestaurantePorId(cadastrado.getId());
        long durationSecondCall = System.currentTimeMillis() - startSecondCall;

        assertThat(durationSecondCall)
                .as("A segunda chamada (cache) deveria ser mais rápida")
                .isLessThan(durationFirstCall);

        System.out.printf("DB: %dms | Cache: %dms%n", durationFirstCall, durationSecondCall);
    }
}
