package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    //busca cliente pelo email
    Optional<Cliente> findByEmail(String email);
    
    //lista clientes ativos
    List<Cliente> findByAtivoTrue();

    //busca por nome parcial
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    //verifica se o email já está cadastrado
    boolean existsByEmail(String email);
}