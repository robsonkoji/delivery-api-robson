package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Buscar por categoria
    List<Restaurante> findByCategoria(String categoria);

    // Buscar restaurantes ativos
    List<Restaurante> findByAtivoTrue();

    // Buscar por faixa de taxa de entrega menor ou igual
    @Query("SELECT r FROM Restaurante r WHERE r.taxaEntrega <= :taxa")
    List<Restaurante> findByTaxaEntregaLessThanEqual(@Param("taxa") BigDecimal taxa);

    // Query customizada - top 5 restaurantes por nome (ordem alfabética)
    @Query("SELECT r FROM Restaurante r ORDER BY r.nome ASC")
    List<Restaurante> findTop5ByOrderByNomeAsc();

    Optional<Restaurante> findByNome(String nome);



}