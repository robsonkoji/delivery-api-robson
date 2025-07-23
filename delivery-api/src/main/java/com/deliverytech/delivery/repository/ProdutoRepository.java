package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    //produtos de um restaurante
    List<Produto> findByRestauranteId(Long restauranteId);

    //produtos disponíveis
    List<Produto> findByDisponivelTrue();

    //produtos por categoria
    List<Produto> findByCategoria(String categoria);

    //produtos mais baratos que um valor
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);

    Iterable<Produto> findByRestaurante(Restaurante rSushiYama);
    
}
