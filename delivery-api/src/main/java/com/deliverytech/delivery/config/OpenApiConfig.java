package com.deliverytech.delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DeliveryTech API")
                .version("v1.0")
                .description("API para gerenciamento de pedidos, restaurantes, clientes e relatórios.")
                .contact(new Contact()
                    .name("Suporte DeliveryTech")
                    .url("https://deliverytech.com/suporte")
                    .email("suporte@deliverytech.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"))
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Desenvolvimento"),
                new Server().url("https://api.deliverytech.com").description("Produção")
            ))
            .tags(List.of(
                new Tag().name("Clientes").description("Operações relacionadas aos clientes"),
                new Tag().name("Restaurantes").description("Gestão de restaurantes"),
                new Tag().name("Pedidos").description("Processamento de pedidos"),
                new Tag().name("Relatórios").description("Relatórios e estatísticas")
            ))
            // Definição do esquema de segurança JWT Bearer
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                )
            )
            // Aplicação global do requisito de segurança
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
