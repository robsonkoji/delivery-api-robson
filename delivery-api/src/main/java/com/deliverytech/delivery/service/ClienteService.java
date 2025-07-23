package com.deliverytech.delivery.service;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Cadastrar novo cliente
     */
    public Cliente cadastrar(Cliente cliente) {
        if (emailJaCadastrado(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + cliente.getEmail());
        }
        validarDadosCliente(cliente);
        cliente.setAtivo(true);
        return clienteRepository.save(cliente);
    }

    /**
     * Listar todos os clientes ativos
     */
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }   

    /**
     * Buscar cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Atualizar dados do cliente
     */
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(clienteAtualizado.getEmail())
            && emailJaCadastrado(clienteAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já em uso: " + clienteAtualizado.getEmail());
        }

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        return clienteRepository.save(cliente);
    }

    /**
     * Inativar cliente
     */
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    /*
     * Reativar cliente
     */
    public void reativar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        if (cliente.isAtivo()) {
            throw new IllegalArgumentException("Cliente já está ativo");
        }

        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    /**
     * Verificar se o e-mail já está cadastrado
     */
    @Transactional(readOnly = true)
    public boolean emailJaCadastrado(String email) {
        return clienteRepository.existsByEmail(email);
    }

    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
    }
    /**
     * Buscar cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail não pode ser vazio");}
        if (!email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido");}
        return clienteRepository.findByEmail(email);
    }


    /**
     * Buscar clientes por nome parcial
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorNome'");}
    return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
}
