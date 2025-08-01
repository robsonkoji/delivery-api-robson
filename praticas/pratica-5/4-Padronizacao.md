# 4. ✅ Padronização

A padronização foi um dos pilares principais deste projeto, garantindo coerência entre os endpoints, respostas previsíveis para o consumidor da API, melhor integração com frontend e facilidade na manutenção e evolução do sistema. Todas as respostas e comportamentos seguem um modelo consistente, tanto em casos de sucesso quanto de erro.

### ✅ Estrutura de Resposta Uniforme
Todas as respostas da API, independentemente do endpoint ou status HTTP, seguem uma estrutura padronizada no formato JSON, encapsulando os dados e metainformações relevantes:

```json
{
  "dados": {
    "id": 1,
    "nome": "Produto Exemplo"
  },
  "mensagem": "Operação realizada com sucesso",
  "status": 200,
  "timestamp": "2025-08-01T20:00:00Z"
}
```

### Campos:
- `dados`: Corpo da resposta com o conteúdo solicitado ou retornado (objeto, lista, etc).

- `mensagem`: Texto descritivo sobre o resultado da operação.

- `status`: Código HTTP associado à operação.

- `timestamp`: Data/hora em que a resposta foi gerada.

Essa padronização foi aplicada em todas as operações de CRUD, para todas as entidades (`Cliente`, `Restaurante`, `Produto`, `Pedido`).

---


### ✅ Tratamento Centralizado de Exceções
Utilizado um `@ControllerAdvice` customizado para capturar e tratar exceções de forma centralizada e padronizada. Isso inclui:

- Validações de dados (`MethodArgumentNotValidException`)

- Entidades não encontradas (`EntidadeNaoEncontradaException`)

- Conflitos de negócio (`EntidadeJaExisteException`, `IllegalStateException`)

- Erros genéricos ou inesperados

Exemplo de resposta para erro de validação:

```json
{
  "dados": null,
  "mensagem": "Dados inválidos",
  "status": 400,
  "timestamp": "2025-08-01T20:00:00Z",
  "erros": [
    {
      "campo": "email",
      "mensagem": "Formato inválido"
    },
    {
      "campo": "nome",
      "mensagem": "Campo obrigatório"
    }
  ]
}
```
### ✅ Códigos HTTP corretos por operação
|Operação	            |Status HTTP Utilizado        |
|-----------------------|-----------------------------|
|Criação de recurso	    |`201 Created`                |
|Leitura bem-sucedida	|`200 OK`                     |
|Atualização de recurso	|`200 OK`                     |
|Exclusão de recurso	|`204 No Content`             |
|Recurso não encontrado	|`404 Not Found`              |
|Dados inválidos	    |`400 Bad Request`            |
|Conflito de negócio	|`409 Conflict`               |
|Erro interno	        |`500 Internal Server Error`  |

### ✅ Headers HTTP apropriados
* Em requisições de criação (POST), a resposta inclui o header Location com o URI do novo recurso.

* Todas as respostas contêm:

    - `Content-Type: application/json`

    - Headers de CORS configurados (se necessário)

* Respostas com paginação incluem headers customizados:

    - `X-Total-Elements`, `X-Total-Pages`, `X-Current-Page`, `X-Page-Size`

### ✅ Paginação implementada com consistência
* Todos os endpoints de listagem (GET /api/...) suportam paginação com os parâmetros:

    - page (padrão = 0)

    - size (padrão = 10)

    - sort (ex: nome,asc)

* As respostas trazem:

    - Campo dados: lista paginada de registros

    - Campo mensagem: descrição da operação

    - Metadados de paginação nos headers ou no corpo da resposta

Exemplo de resposta paginada:

```json
{
  "dados": [
    { "id": 1, "nome": "Produto A" },
    { "id": 2, "nome": "Produto B" }
  ],
  "mensagem": "Listagem realizada com sucesso",
  "status": 200,
  "timestamp": "2025-08-01T20:00:00Z",
  "paginacao": {
    "paginaAtual": 0,
    "totalPaginas": 5,
    "totalElementos": 50,
    "tamanhoPagina": 10
  }
}
```

### ✅ Benefícios da padronização aplicada
- 🔁 Facilidade de integração com front-end, reduzindo necessidade de adaptações.

- 🧪 Facilidade nos testes automatizados, com validação uniforme das respostas.

- 📚 Melhor documentação e entendimento dos comportamentos da API.

- ⚙️ Base sólida para geração de SDKs e clients automáticos, por seguir convenções modernas.

- 🛠️ Manutenção e evolução mais seguras, com menor risco de efeitos colaterais.