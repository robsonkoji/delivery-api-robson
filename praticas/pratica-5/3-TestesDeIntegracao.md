# 3. ✅ Testes de Integração

Os testes de integração foram desenvolvidos com o objetivo de garantir a estabilidade, consistência e confiabilidade da API, cobrindo os principais fluxos de negócio e validando o comportamento dos endpoints em um ambiente controlado, porém muito próximo do ambiente real.

### 🛠️ Ferramentas e Tecnologias Utilizadas
**JUnit 5** – Framework base para testes em Java.

**Spring Boot Test + Spring MockMvc** – Para simular requisições HTTP reais e validar as respostas da aplicação.

**Banco de dados H2 (em memória)** – Permitiu a simulação de operações com persistência real sem impactar o banco de dados de produção ou desenvolvimento.

**ObjectMapper (Jackson)** – Para serialização/deserialização de JSON nos testes.

**Perfil test isolado** – Permite rodar testes com configurações específicas (datasource H2, portas customizadas, etc).

### ✅ Cobertura de Testes

* Todos os controllers foram testados com MockMvc, simulando chamadas HTTP reais:

- `ClienteController`

- `RestauranteController`

- `ProdutoController`

- `PedidoController`

* Cada controller foi testado com foco em:

    - Cenários de sucesso

    - Cenários de falha

    - Códigos HTTP esperados

    - Corpo da resposta (contrato JSON)

    - Headers e metadados (como paginação)

### 📋 Cenários obrigatórios cobertos
|Cenário de Teste	                         |Implementado	     |Código HTTP Esperado|
|--------------------------------------------|-------------------|--------------------|
|✅ Criação bem-sucedida	                    |Sim	            |`201 Created`         |
|✅ Consulta por ID existente	            |Sim	            |`200 OK`              |
|✅ Consulta por ID inexistente	            |Sim	            |`404 Not Found`       |
|✅ Dados inválidos no corpo da requisição	|Sim	            |`400 Bad Request`     |
|✅ Conflito de dados (duplicidade, regras)	|Sim	            |`409 Conflict`        |
|✅ Paginação de resultados	                |Sim	            |`200 OK` com headers  |


----


### 📌 Detalhes Técnicos
* Utilização de `@SpringBootTest` e `@AutoConfigureMockMvc` para carregar o contexto completo e permitir testes com endpoints reais.

* Utilização de `@Transactional` em testes que envolvem persistência, garantindo rollback automático após cada execução.

* Os DTOs de request são validados com `@Valid`, e os testes cobrem casos de violação dessas regras (ex: campos obrigatórios vazios, valores inválidos).

* A validação da resposta é feita com `MockMvcResultMatchers` e `jsonPath`, verificando:

    - Presença de atributos esperados no JSON

    - Tipos de dados corretos

    - Mensagens de erro padronizadas




### 🧪 Exemplos de testes implementados
* **POST /api/clientes**

    - Criação de cliente com dados válidos → retorna 201

    - Criação com e-mail inválido → retorna 400

* **GET /api/restaurantes/{id}**

    - Restaurante existente → retorna 200 com os dados

    - Restaurante inexistente → retorna 404

* **PUT /api/produtos/{id}**

    - Atualização de nome e disponibilidade do produto → retorna 200

    - Atualização com preço negativo → retorna 400

* **GET /api/pedidos?page=0&size=10**

    - Verifica metadados de paginação (totalElements, totalPages, etc.)

### 📤 Collection Postman

* Uma collection Postman foi criada para testes manuais e documentação prática da API.

Localizada em:

```bash
/docs/postman_collection.json
```

* A collection cobre todos os endpoints e está organizada por controller.

* Inclui:

    - Exemplos de payloads de entrada válidos e inválidos

    - Respostas esperadas (200, 201, 400, 404, 409)

    - Testes com filtros, paginação e headers

### 🔍 Benefícios dos testes de integração implementados

* Detecção precoce de erros entre camadas (controller ↔ service ↔ repository).

* Confiança na refatoração, com cobertura de fluxos críticos.

* Contratos validados automaticamente, evitando inconsistências entre documentação e código.

* Base para automação de CI/CD, permitindo execução dos testes em pipelines.