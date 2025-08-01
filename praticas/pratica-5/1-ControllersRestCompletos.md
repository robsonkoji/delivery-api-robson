# 1. ✅ Controllers REST Completos

## A aplicação foi estruturada em torno de quatro controladores REST, cada um responsável por um domínio da aplicação:

|Controller	            |Responsabilidade                                              |
|-----------------------|--------------------------------------------------------------|
|`ClienteController`	| Gerencia o ciclo de vida de clientes (CRUD completo)         |
|`RestauranteController`| Cadastro, atualização, remoção e listagem de restaurantes    |
|`ProdutoController`  	| CRUD de produtos, com vínculo ao restaurante                 |
|`PedidoController`  	| Registro de pedidos, cálculo de total, status e cancelamento |



Cada controller segue os princípios de arquitetura RESTful, com URLs semânticas, métodos HTTP apropriados, e respostas condizentes com o tipo de operação realizada.

### 🔧 Endpoints RESTful Implementados

Todos os controladores expõem endpoints completos, com suporte para:

- `GET`: Busca individual e listagem paginada

- `POST`: Criação de novos registros

- `PUT`: Atualização total do recurso

- `PATCH`: Atualizações parciais (ex: status do pedido)

- `DELETE`: Exclusão lógica ou permanente

---


### 🧾 Uso de DTOs e Validações

Todas as entradas do usuário são encapsuladas em DTOs de requisição (`RequestDTO`), contendo validações declarativas com:

- `@NotNull`, `@NotBlank`, `@Email`, `@Min`, `@Size`, etc.

As saídas da API seguem um padrão unificado de DTOs de resposta (`ResponseDTO`), promovendo:

- Desacoplamento das entidades JPA

- Encapsulamento de dados sensíveis

- Padrão de comunicação claro entre cliente e servidor

---

### ✅ Validação com `@Valid`

- O Spring valida automaticamente os DTOs de entrada usando `@Valid` nos parâmetros do controller.

- Mensagens de erro específicas são retornadas em caso de violação de regra de negócio ou inconsistência nos dados.

---

### ❌ Tratamento Global de Erros
Foi implementado um tratador global de exceções usando `@ControllerAdvice`, permitindo capturar e padronizar erros em todo o sistema:

|Exceção Lançada	                | Código HTTP	| Situação Exemplo                        |
|-----------------------------------|---------------|-----------------------------------------|
|MethodArgumentNotValidException	|400	        |Dados inválidos no corpo da requisição   |
|ResourceNotFoundException	        |404	        |Cliente, produto ou pedido não encontrado|
|DataConflictException	            |409	        |Tentativa de criar recurso duplicado     |
|Exception	                        |500	        |Falhas inesperadas no servidor           |

Todos os erros seguem um formato de resposta consistente, contendo:

```json
{
  "status": 400,
  "mensagem": "Campo 'nome' não pode ser vazio",
  "timestamp": "2025-08-01T20:00:00Z",
  "camposInvalidos": [
    {
      "campo": "nome",
      "mensagem": "não pode estar em branco"
    }
  ]
}
```

---

### 🔁 Padrões de Projeto Adotados

- Separação clara entre camadas (`Controller` → `Service` → `Repository`)

- Regras de negócio centralizadas na camada `Service`

- Uso de interfaces e injeção de dependência via Spring

- Conversão entre DTOs e entidades feita com `ModelMapper` ou `MapStruct`



### 📋 Exemplo de Endpoints Implementados

**POST /clientes**
Cria um novo cliente com validação de CPF e e-mail únicos.

**GET /restaurantes?categoria=Japonesa**
Lista restaurantes filtrando por categoria, com paginação.

**PATCH /pedidos/{id}/status**
Atualiza o status de um pedido (`REALIZADO` → `ENTREGUE`).

**DELETE /produtos/{id}**
Remove um produto do restaurante.

---

