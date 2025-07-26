# 📦 Camada de Services - Projeto Delivery API

Este documento apresenta os serviços implementados como parte da arquitetura do projeto `delivery-api`, desenvolvidos com Spring Boot. A camada de serviços é responsável por aplicar as regras de negócio e orquestrar o uso de repositórios e entidades.



## ✅ Entregáveis - Camada de Services

### 1. Interfaces de Service

As seguintes interfaces foram criadas, promovendo uma arquitetura orientada a contratos, facilitando a testabilidade e desacoplamento entre as camadas:

- `ClienteService`
- `RestauranteService`
- `ProdutoService`
- `PedidoService`

---
### 2. Implementações de Service

Cada interface possui sua respectiva implementação:

- `ClienteServiceImpl`
- `RestauranteServiceImpl`
- `ProdutoServiceImpl`
- `PedidoServiceImpl`

Todas as implementações estão anotadas com `@Service` e seguem o padrão de injeção de dependência via construtor (preferencialmente com `@RequiredArgsConstructor` do Lombok).


---
### 3. Anotações

- `@Service`: usada para marcar classes de serviço que implementam as regras de negócio.
- `@Transactional`: utilizada em métodos que realizam operações de escrita no banco de dados, garantindo consistência e rollback em caso de falhas.

Exemplo:
```java
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    
    @Transactional
    public Pedido criarPedido(PedidoRequest request) {
        // lógica do pedido
    }
}
```

---
### 4. Regras de Negócio Implementadas

As principais regras de negócio implementadas são:

-✅ **Email único:** não permitir cadastro de clientes com e-mail já existente.

-✅ **Cliente ativo:** somente clientes ativos podem realizar pedidos.

-✅ **Restaurante ativo:** pedidos só podem ser realizados em restaurantes ativos.

-✅ **Produtos do restaurante:** validar que os produtos pertencem ao restaurante selecionado.

-✅ **Disponibilidade do produto:** impedir inclusão de produtos indisponíveis em pedidos.

-✅ **Cálculo de total:** cálculo automático de subtotal + taxa de entrega.

-✅ **Status do pedido:** validar transições de status permitidas.

-✅ **Cancelamento:** apenas pedidos com status RECEBIDO podem ser cancelados.


---
### 🗂️ Estrutura de Pacotes

```bash
com.deliverytech.delivery
├── service
│   ├── ClienteService.java
│   ├── RestauranteService.java
│   ├── ProdutoService.java
│   └── PedidoService.java
└── service.impl
    ├── ClienteServiceImpl.java
    ├── RestauranteServiceImpl.java
    ├── ProdutoServiceImpl.java
    └── PedidoServiceImpl.java
```


---
### 📌 Observações Técnicas

- Todos os serviços seguem boas práticas de clean code.
- As exceções são lançadas utilizando:

    `EntityNotFoundException` → entidades não encontradas

    `BusinessException` → violação de regras de negócio
    
    `ValidationException` → erros de entrada
    
    `TransactionException` → falhas transacionais








