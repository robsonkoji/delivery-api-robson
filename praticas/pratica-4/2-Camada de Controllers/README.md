# 🌐 Camada de Controllers - Projeto Delivery API

Este documento apresenta a implementação da **camada de controllers** do projeto `delivery-api`, responsável por expor os endpoints RESTful que interagem com os serviços de negócio.

---

## ✅ Entregáveis - Camada de Controllers

### 1. Controllers REST Implementados


Foram criados **4 controllers REST** separados por responsabilidade:

- `ClienteController` – gerenciamento de clientes
- `RestauranteController` – gerenciamento de restaurantes
- `ProdutoController` – gerenciamento de produtos
- `PedidoController` – fluxo completo de pedidos

Todos os controllers seguem o padrão `@RestController` e estão organizados conforme boas práticas REST.

---

### 2. Convenções HTTP Seguidas

Os endpoints foram construídos seguindo **convenções RESTful**, como:

| Ação                        | Verbo HTTP | Status Code Esperado | Exemplo de Endpoint                 |
|----------------------------|------------|-----------------------|-------------------------------------|
| Criar recurso              | `POST`     | `201 Created`         | `/clientes`                         |
| Buscar por ID              | `GET`      | `200 OK`              | `/pedidos/{id}`                     |
| Listar todos               | `GET`      | `200 OK`              | `/restaurantes`                     |
| Atualizar recurso (status) | `PATCH`    | `200 OK`              | `/pedidos/{id}/status`             |
| Deletar/cancelar recurso   | `DELETE`   | `204 No Content`      | `/pedidos/{id}` (cancelar pedido)  |

---

### 3. Anotações Utilizadas

Todos os controllers estão anotados com:

- `@RestController`: define a classe como um controller REST.
- `@RequestMapping`: define o path base de cada recurso (ex: `/clientes`, `/pedidos`).
- `@PostMapping`, `@GetMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`: usados conforme a operação.

Exemplo:
```java
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteRequest request) {
        Cliente cliente = clienteService.cadastrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClienteResponse(cliente));
    }
}
```

---

### 4. Status Codes HTTP Corretos

A resposta dos endpoints utiliza os códigos HTTP corretos conforme a ação realizada:

`200 OK:` retorno de buscas e atualizações

`201 Created:` criação de recurso

`204 No Content:` deleção sem retorno de body

`400 Bad Request:` dados inválidos

`404 Not Found:` recurso inexistente

`422 Unprocessable Entity:` violação de regra de negócio (via BusinessException)


---

### 🗂️ 5. Estrutura de Pacotes

```bash
com.deliverytech.delivery
└── controller
    ├── ClienteController.java
    ├── RestauranteController.java
    ├── ProdutoController.java
    └── PedidoController.java
```

---

### 🧪 6. Exemplos de Endpoints

#### Criar Pedido
```bash
POST /pedidos
Content-Type: application/json

{
  "clienteId": 1,
  "restauranteId": 2,
  "enderecoEntrega": "Rua A, 123",
  "itens": [
    { "produtoId": 10, "quantidade": 2 }
  ]
}
```
#### Resposta:
```bash
201 Created
Location: /pedidos/1
```


---

#### Atualizar Status do Pedido

```bash
PATCH /pedidos/1/status
Content-Type: application/json

{
  "status": "EM_PREPARACAO"
}
```
#### Resposta:
```bash
200 OK
```


---

### 📌 Observações Técnicas
- Utilização de `ResponseEntity` para personalização de status e headers.

- Validação de entrada com `@Valid` e DTOs de request.

- Erros tratados com exceções customizadas: `EntityNotFoundException`, `BusinessException`, `ValidationException`.
