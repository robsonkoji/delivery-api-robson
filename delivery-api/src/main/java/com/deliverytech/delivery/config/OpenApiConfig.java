package com.deliverytech.delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            // Informações da API
            .info(new Info()
                .title("DeliveryTech API")
                .version("1.0.0")
                .description("API para gerenciamento de pedidos, restaurantes, clientes e relatórios.")
                .contact(new Contact()
                    .name("Suporte DeliveryTech")
                    .url("https://deliverytech.com/suporte")
                    .email("suporte@deliverytech.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT"))
            )
            // Servidores para diferentes ambientes
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Ambiente de Desenvolvimento"),
                new Server().url("https://api.deliverytech.com").description("Ambiente de Produção")
            ))
            // Organização de endpoints por tags
            .tags(List.of(
                new Tag().name("Clientes").description("Operações relacionadas aos clientes"),
                new Tag().name("Restaurantes").description("Gestão de restaurantes"),
                new Tag().name("Pedidos").description("Processamento de pedidos"),
                new Tag().name("Relatórios").description("Relatórios e estatísticas"),
                new Tag().name("Autenticação").description("Endpoints para login e registro")
            ))
            // Configuração da segurança JWT
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Autenticação via token JWT")
                )
            )
            // Aplicação global da segurança JWT em todos os endpoints
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
