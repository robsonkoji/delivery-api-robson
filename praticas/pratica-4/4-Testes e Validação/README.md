# ✅ Testes e Validação

Este documento apresenta os testes realizados na aplicação `delivery-api`, cobrindo os serviços (camada de negócio), os controllers (camada de API REST), além da documentação dos endpoints e uma collection Postman para validação manual.

---

## 🧪 1. Testes Unitários dos Services

Foram implementados testes unitários utilizando **JUnit 5** e **Mockito** para simular os repositórios e validar as regras de negócio dos serviços.

### Tecnologias:
- `JUnit 5`
- `Mockito`
- `Spring Boot Test`
- `AssertJ`

### Exemplo – `ClienteServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void deveCadastrarClienteComSucesso() {
        ClienteRequest request = new ClienteRequest("João", "joao@email.com", "11999999999", "Rua A");
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);

        Cliente clienteSalvo = clienteService.cadastrarCliente(request);

        assertThat(clienteSalvo.getNome()).isEqualTo("João");
        verify(clienteRepository, times(1)).save(any());
    }

    @Test
    void deveLancarExcecaoAoCadastrarEmailDuplicado() {
        when(clienteRepository.existsByEmail("teste@email.com")).thenReturn(true);

        ClienteRequest request = new ClienteRequest("Ana", "teste@email.com", "11999999999", "Rua B");

        assertThatThrownBy(() -> clienteService.cadastrarCliente(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Cliente com email já cadastrado");
    }
}
```

---

## 🔁 2. Testes de Integração dos Controllers

Utilizando @SpringBootTest com MockMvc, foram criados testes para os controllers, validando os endpoints da API de ponta a ponta.

### Tecnologias:

- `Spring Boot Test`

- `MockMvc`

- `ObjectMapper`(Jackson)

#### Exemplo – `ClienteControllerIntegrationTest.java`
```java
@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarClienteRetornando201() throws Exception {
        ClienteRequest request = new ClienteRequest("Lucas", "lucas@email.com", "11999988888", "Av. Brasil");

        mockMvc.perform(post("/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Lucas"));
    }
}
```
📬 3. Collection Postman / Insomnia
A collection completa com os endpoints REST da API foi criada para facilitar testes manuais com Postman ou Insomnia.

Conteúdo da Collection:
POST /clientes

- `GET /clientes/{id}`
- `PUT /clientes/{id}`
- `PATCH /clientes/{id}/ativar`
- `GET /clientes/ativos`
- `POST /pedidos`
- `GET /pedidos/cliente/{id}`
- `PATCH /pedidos/{id}/status`
- entre outros...

🔗 Link da Collection: 📥 Download collection-delivery-api.postman.json

---

## 📚 4. Documentação dos Endpoints

### Padrões utilizados:
- Status codes apropriados (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`)

- Mensagens de erro padronizadas via `ExceptionHandler`

- Estrutura consistente de requisição/resposta

### Exemplo – Cadastro de Cliente

#### Requisição:

```json
{
  "nome": "Carlos Silva",
  "email": "carlos@email.com",
  "telefone": "11999999999",
  "endereco": "Rua A, 123"
}
```
#### Resposta:
```json
{
  "id": 1,
  "nome": "Carlos Silva",
  "email": "carlos@email.com",
  "telefone": "11999999999",
  "ativo": true
}
```
#### Erros esperados:
```json
{
  "status": 400,
  "mensagem": "Email inválido"
}
```

---

### 📂 Estrutura de Pastas de Testes

```plaintext
src/test/java
└── com.deliverytech.delivery
    ├── service
    │   ├── ClienteServiceTest.java
    │   ├── PedidoServiceTest.java
    │   └── RestauranteServiceTest.java
    └── controller
        ├── ClienteControllerIntegrationTest.java
        ├── PedidoControllerIntegrationTest.java
        └── ProdutoControllerIntegrationTest.java
```

----

### ✅ Cobertura de Testes

- [x]Testes unitários cobrindo regras de negócio
- [x]Testes de integração simulando chamadas reais
- [x]Testes de erro e validação de dados inválidos
- [x]Testes de endpoints REST completos via Postman