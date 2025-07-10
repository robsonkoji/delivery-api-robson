# 🗂️ Repositórios JPA

Interfaces criadas com Spring Data JPA para acesso às entidades do sistema:

## ClienteRepository
- `findByEmail`
- `findByAtivoTrue`

## RestauranteRepository
- `findByNomeContainingIgnoreCase`
- `findByCategoria`
- `findByAtivoTrueOrderByAvaliacaoDesc`

## ProdutoRepository
- `findByRestauranteId`
- `findByCategoria`
- `findByDisponivelTrue`

## PedidoRepository
- `findByClienteId`
- `findByStatus`
- `buscarPorDataEntre` (usando `@Query`)

Testes realizados com banco H2 em memória.