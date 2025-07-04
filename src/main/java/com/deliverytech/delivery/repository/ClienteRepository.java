package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por email (método derivado)
    Optional<Cliente> findByEmail(String email);

    // Verificar se email já existe
    boolean existsByEmail(String email);

    // Buscar clientes ativos
    List<Cliente> findByAtivoTrue();

    // Buscar clientes por nome (contendo)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // Buscar clientes por telefone
    Optional<Cliente> findByTelefone(String telefone);

    // Query customizada - clientes com pedidos
    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.pedidos p WHERE c.ativo = true")
    List<Cliente> findClientesComPedidos();

    // Query nativa - clientes por cidade
    @Query(value = "SELECT * FROM clientes WHERE endereco LIKE CONCAT('%', :cidade, '%') AND ativo = true", nativeQuery = true)
    List<Cliente> findByCidade(@Param("cidade") String cidade);

    // Contar clientes ativos
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true")
    Long countClientesAtivos();
}