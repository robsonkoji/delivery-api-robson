package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByAtivoTrue();
    List<Cliente> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    List<Cliente> findByTelefoneContainingIgnoreCaseAndAtivoTrue(String telefone);
    List<Cliente> findByEnderecoContainingIgnoreCaseAndAtivoTrue(String endereco);
    Optional<Cliente> findByIdAndAtivoTrue(Long id);
    Optional<Cliente> findByEmailAndAtivoTrue(String email);
    Optional<Cliente> findByIdAndAtivoFalse(Long id);
    Optional<Cliente> findByEmailAndAtivoFalse(String email);
    List<Cliente> findByTelefoneContainingAndAtivoTrue(String telefone);
    List<Cliente> findByEnderecoContainingAndAtivoTrue(String endereco);
    Optional<Cliente> findByUsuario(Usuario usuario);

}