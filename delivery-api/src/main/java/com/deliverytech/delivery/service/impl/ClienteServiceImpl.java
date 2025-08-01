package com.deliverytech.delivery.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente cadastrarCliente(ClienteRequest request) {
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cliente com email já cadastrado");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(request.getEndereco());
        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
    }

    @Override
    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com email: " + email));
    }

    @Override
    public Cliente atualizarCliente(Long id, ClienteRequest request) {
        Cliente cliente = buscarClientePorId(id);

        if (clienteRepository.existsByEmail(request.getEmail()) &&
            !cliente.getEmail().equals(request.getEmail())) {
            throw new BusinessException("Cliente com email já cadastrado");
        }

        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(request.getEndereco());

        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente ativarDesativarCliente(Long id) {
        Cliente cliente = buscarClientePorId(id);
        if (cliente.isAtivo()) {
            cliente.inativar();
        } else {
            cliente.reativar();
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarClientesAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    @Override
    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);  
    }

    @Override
    public List<Cliente> buscarClientesPorTelefone(String telefone) {
        return clienteRepository.findByTelefoneContainingAndAtivoTrue(telefone);
    }

    @Override
    public List<Cliente> buscarClientesPorEndereco(String endereco) {
        return clienteRepository.findByEnderecoContainingIgnoreCaseAndAtivoTrue(endereco);
    }

    @Override
    public Cliente inativarCliente(Long id) {
        Cliente cliente = buscarClientePorId(id);
        cliente.inativar();
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente ativarCliente(Long id) {
        Cliente cliente = buscarClientePorId(id);
        cliente.reativar();
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }
}
