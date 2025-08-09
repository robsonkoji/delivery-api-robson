package com.deliverytech.delivery.cache;

import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.service.RestauranteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void setupSecurityContext() {
        // Cria um usuário "fake" para simular autenticação no Spring Security
        Usuario usuarioFake = new Usuario();
        usuarioFake.setId(1L);
        usuarioFake.setEmail("admin@email.com");

        var authentication = new UsernamePasswordAuthenticationToken(usuarioFake, null, usuarioFake.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        // Limpa o contexto para não afetar outros testes
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveDemonstrarGanhoDePerformanceComCache() {
        // Limpa o cache antes do teste
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        // Cria restaurante para testar
        RestauranteRequest request = new RestauranteRequest();
        request.setNome("Restaurante Performance");
        request.setCategoria("Italiana");
        request.setTaxaEntrega(new BigDecimal("12.00"));
        request.setTelefone("11999998888");
        request.setEndereco("Rua Teste, 123");

        RestauranteResponse cadastrado = restauranteService.cadastrarRestaurante(request);

        // Medir o tempo da primeira chamada (deve acessar DB)
        long startFirstCall = System.currentTimeMillis();
        RestauranteResponse primeiraChamada = restauranteService.buscarRestaurantePorId(cadastrado.getId());
        long durationFirstCall = System.currentTimeMillis() - startFirstCall;

        // Medir o tempo da segunda chamada (deve vir do cache)
        long startSecondCall = System.currentTimeMillis();
        RestauranteResponse segundaChamada = restauranteService.buscarRestaurantePorId(cadastrado.getId());
        long durationSecondCall = System.currentTimeMillis() - startSecondCall;

        // Validar que o resultado da chamada é igual
        assertThat(primeiraChamada).isEqualTo(segundaChamada);

        // Validar que a segunda chamada foi mais rápida (tempo menor)
        assertThat(durationSecondCall).isLessThan(durationFirstCall);

        System.out.println("Tempo primeira chamada (DB): " + durationFirstCall + "ms");
        System.out.println("Tempo segunda chamada (Cache): "+ durationSecondCall + "ms");
        System.out.println("Tempo terceira chamada (Cache): "+ durationSecondCall + "ms");
    }
}
 