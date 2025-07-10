# 🚚 Delivery Tech API

Sistema de delivery desenvolvido com Java 21 e Spring Boot, utilizando recursos modernos da linguagem e uma arquitetura simples para testes e desenvolvimento local.

## 🛠️ Tecnologias Utilizadas

- Java 21 (LTS)
- Spring Boot 3.5.5
- Spring Web
- Spring Data JPA
- Banco de Dados: H2 (em memória)
- Build Tool: Maven

---

## ⚙️ Recursos Modernos da Linguagem

Este projeto utiliza funcionalidades recentes do Java:

✅ Records (Java 14+)  
✅ Text Blocks (Java 15+)  
✅ Pattern Matching (Java 17+)  
✅ Virtual Threads (Java 21)

---

## ▶️ Como Executar o Projeto

Certifique-se de ter o **JDK 21** instalado.

```bash
# Clone o repositório
git clone https://github.com/robsonkoji/delivery-api-robson.git
cd delivery-api-robson

# Execute a aplicação
./mvnw spring-boot:run
```
Acesse a aplicação em: http://localhost:8080/health

## Endpoints Disponíveis

| Método | Endpoint                           | Descrição                                       |
| ------ | ---------------------------------- | ----------------------------------------------- |
| GET    | `/health`                          | Verifica o status da aplicação e versão do Java |
| GET    | `/info`                            | Retorna informações da aplicação                |
| GET    | `/h2-console`                      | Console web do banco H2 (em memória)            |
| POST   | `/clientes`                        | Cadastra um novo cliente                        |
| POST   | `/restaurantes`                    | Cadastra um novo restaurante                    |
| POST   | `/restaurantes/{id}/produtos`      | Adiciona um produto a um restaurante            |
| POST   | `/pedidos`                         | Cria um novo pedido                             |
| PUT    | `/pedidos/{id}/status?status=NOVO` | Atualiza o status de um pedido                  |
| GET    | `/pedidos`                         | Lista todos os pedidos                          |


## Exemplos de Requisições

Use ferramentas como Postman ou Insomnia. Abaixo alguns exemplos:

## Criar Cliente
```bash
POST /clientes
Content-Type: application/json

{
  "nome": "João",
  "email": "joao@email.com",
  "telefone": "11999999999"
}
```
## Criar Restaurante
```bash
POST /restaurantes
Content-Type: application/json

{
  "nome": "Pizza Top",
  "categoria": "Italiana"
}
```
## Adicionar Produto ao Restaurante
```bash
POST /restaurantes/1/produtos
Content-Type: application/json

{
  "nome": "Pizza Calabresa",
  "descricao": "Deliciosa",
  "preco": 45.50,
  "categoria": "Pizza"
}
```
## Criar Pedido

```bash
POST /pedidos
Content-Type: application/json

{
  "clienteId": 1,
  "restauranteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ]
}
```
## Adicionar Produto ao Restaurante

```bash
POST /restaurantes/1/produtos
Content-Type: application/json

{
  "nome": "X-Burguer",
  "descricao": "Pão, carne e queijo",
  "preco": 18.50,
  "categoria": "Lanche"
}
```
## Atualizar Status do Pedido

```bash
PUT /pedidos/1/status?status=ENTREGUE
```

## Estrutura do Projeto
```bash
├── controller        # Endpoints REST
├── service           # Regras de negócio
├── entity            # Entidades JPA (Pedido, Produto, etc.)
├── repository        # Interfaces JPA
├── DeliveryApiApplication.java
```

## Testes com Postman/Insomnia

Você pode testar os endpoints usando o Postman, Insomnia ou qualquer API client.

✅ Recomenda-se importar uma Collection JSON (caso tenha)
✅ Todas as respostas estão em application/json
✅ A aplicação roda em http://localhost:8080

## Desenvolvedor

Robson Koji

Turno: Tarde

Desenvolvido com 💻 JDK 21 e Spring Boot 3.5.5

