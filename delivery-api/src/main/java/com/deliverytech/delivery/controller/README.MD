# 🌐 Endpoints REST

## ClienteController

| Método | Endpoint                  | Descrição                  |
| ------ | ----------------          | -------------------------- |
| POST   | `/clientes`               | Cadastrar novo cliente     |
| GET    | `/clientes`               | Listar todos os clientes   |
| GET    | `/clientes/{id}`          | Buscar cliente por ID      |
| PUT    | `/clientes/{id}`          | Atualizar dados do cliente |
| PUT    | `/clientes/{id}/reativar` | Reativar cliente           |
| DELETE | `/clientes/{id}`          | Inativar cliente           |

## RestauranteController

| Método.   | Endpoint                              | Descrição                         |
| --------- | ------------------------------------- | --------------------------------- |
| POST      | `/restaurantes`                       | Cadastrar novo restaurante        |
| GET       | `/restaurantes`                       | Listar todos os restaurantes      |
| GET       | `/restaurantes/{id}`                  | Buscar restaurante por ID         |
| GET       | `/restaurantes/categoria/{categoria}` | Listar restaurantes por categoria |
| PUT       | `/restaurantes/{id}/reativar`         | Ativar restaurante                |
| DELETE    | `/restaurantes/{id}/inativar`         | Inativar restaurante              |

## ProdutoController


| Método | Endpoint                                 | Descrição                             |
| ------ | ---------------------------------------- | ------------------------------------- |
| POST   | `/restaurantes/{restauranteId}/produtos` | Cadastrar produto para um restaurante |
| GET    | `/restaurantes/{restauranteId}/produtos` | Listar produtos por restaurante       |
| GET    | `/produtos`                              | Listar todos produtos                 |

## PedidoController

| Método | Endpoint                                       | Descrição                  |
| ------ | ---------------------------------------------- | -------------------------- |
| POST   | `/pedidos`                                     | Criar novo pedido          |
| GET    | `/pedidos/cliente/{clienteId}`                 | Listar pedidos por cliente |
| PUT    | `/pedidos/{id}/status?status=Indormar o status`| Atualizar status do pedido |
