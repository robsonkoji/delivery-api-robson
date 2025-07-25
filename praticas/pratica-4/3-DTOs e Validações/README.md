# 📦 DTOs e Validações

Este documento descreve a implementação dos **DTOs (Data Transfer Objects)** utilizados no projeto `delivery-api`, incluindo validações de entrada, otimização de respostas e mapeamento entre entidades e DTOs.

---

## ✅ Itens Implementados

### 1. DTOs de Request com Validações

Todos os dados recebidos pelos endpoints são encapsulados em objetos `RequestDTO`, que aplicam validações com **Bean Validation**:

- `@NotBlank`: campos obrigatórios de texto
- `@Email`: formato de e-mail
- `@NotNull`: campos obrigatórios
- `@Size`: tamanho mínimo/máximo de listas ou strings

#### Exemplo – `ClienteRequest.java`
```java
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
}
```

---

### 2. DTOs de Response Otimizados

Os DTOs de resposta (`ResponseDTO`) contêm apenas os dados relevantes que devem ser expostos à API pública, ocultando dados sensíveis ou internos.

#### Exemplo – `ClienteResponse.java`

```java
public class ClienteResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;

    public ClienteResponse(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.ativo = cliente.isAtivo();
    }
}
```
---


### 3. Mapeamento entre Entidades e DTOs

O mapeamento entre entidades e DTOs foi realizado manualmente, de forma simples e objetiva, evitando o uso de bibliotecas externas como MapStruct ou ModelMapper.

#### Exemplo:

```java
public Cliente toEntity(ClienteRequest request) {
    Cliente cliente = new Cliente();
    cliente.setNome(request.getNome());
    cliente.setEmail(request.getEmail());
    cliente.setTelefone(request.getTelefone());
    cliente.setEndereco(request.getEndereco());
    cliente.setAtivo(true);
    return cliente;
}
```

---

### 4. Bean Validation Configurado

A validação automática de campos nos endpoints é feita com:

- `@Valid` nos parâmetros do controller

- Anotações de validação nos atributos dos DTOs

#### Exemplo – Controller

```java
@PostMapping
public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteRequest request) {
    Cliente cliente = clienteService.cadastrarCliente(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ClienteResponse(cliente));
}
```

---

### 🗂️ Estrutura de Pacotes

```bash
com.deliverytech.delivery
├── dto
│   ├── request
│   │   ├── ClienteRequest.java
│   │   ├── RestauranteRequest.java
│   │   ├── ProdutoRequest.java
│   │   └── PedidoRequest.java
│   └── response
│       ├── ClienteResponse.java
│       ├── RestauranteResponse.java
│       ├── ProdutoResponse.java
│       └── PedidoResponse.java
```

---

### 🧪 Exemplo de Validação

##### Requisição inválida
```http
POST /clientes
Content-Type: application/json

{
  "nome": "",
  "email": "emailinvalido",
  "telefone": "",
  "endereco": ""
}
```
##### Resposta:
```http
400 Bad Request

{
  "erros": [
    "Nome é obrigatório",
    "Email inválido",
    "Telefone é obrigatório",
    "Endereço é obrigatório"
  ]
}
```

