# 2. ✅ Documentação Swagger
A documentação da API foi implementada utilizando o SpringDoc OpenAPI, que gera automaticamente uma interface visual interativa com base nas anotações do código-fonte, promovendo transparência, facilidade de testes e integração com outras aplicações.

### 🌐 Acesso à Documentação
A documentação está disponível no ambiente local em:

```bash
http://localhost:8080/swagger-ui/index.html
```

Esse endereço fornece acesso à Swagger UI, uma interface gráfica para explorar e testar todos os endpoints expostos pela API.

---

### ⚙️ Configuração do SpringDoc OpenAPI
A configuração foi realizada com base nas melhores práticas, utilizando o starter `springdoc-openapi-ui`. Foram definidos metadados da API no bean OpenAPI com:

Título: `Delivery API`

Versão: `v1`

Descrição: API RESTful para gerenciamento de clientes, restaurantes, produtos e pedidos

Contato: Nome e e-mail fictícios para referência

Licença: Apache 2.0

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Delivery API")
            .version("v1")
            .description("API RESTful para gerenciamento de clientes, restaurantes, produtos e pedidos")
            .contact(new Contact().name("Equipe Dev").email("dev@deliveryapi.com"))
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
}
```

---

### 🏷️ Agrupamento por Tags
Os endpoints estão organizados por grupos lógicos, facilitando a navegação:

- `Clientes` – Operações relacionadas a clientes

- `Restaurantes` – Cadastro e consulta de restaurantes

- `Produtos` – Gerenciamento de cardápios

- `Pedidos` – Criação, status e histórico de pedidos

A tag de cada endpoint é atribuída por meio da anotação @Tag nos controllers.

---

### 📄 Anotações OpenAPI nos Endpoints
Todos os endpoints da API foram documentados com as seguintes anotações:

- `@Operation(summary = "Descrição curta", description = "Descrição detalhada")`: explica o que o endpoint faz

- `@ApiResponse(responseCode = "200", description = "Requisição bem-sucedida")`: define os códigos de status esperados

- `@Parameter e @RequestBody`: detalham os parâmetros e exemplos de entrada

---

### 📦 DTOs com Exemplo (@Schema(example = "..."))
Todos os DTOs possuem campos com exemplos ilustrativos para facilitar o preenchimento durante testes via Swagger UI. Exemplo:

```java
@Schema(example = "João da Silva")
@NotBlank
private String nome;
```

---

### 🚀 Benefícios da Documentação
-✅ Geração automática da especificação OpenAPI 3.0, disponível em JSON/YAML (/v3/api-docs)

-✅ Interface interativa para testes manuais dos endpoints, sem necessidade de Postman

-✅ Facilita a integração com frontends ou sistemas de terceiros

-✅ Possibilita geração futura de SDKs para diferentes linguagens (ex: TypeScript, JavaScript, Python)

-✅ Melhora o entendimento e usabilidade da API para novos desenvolvedores



