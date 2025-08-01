package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;

import java.util.List;

public interface ClienteService {

    Cliente cadastrarCliente(ClienteRequest request);

    Cliente buscarClientePorId(Long id);

    Cliente buscarClientePorEmail(String email);

    Cliente atualizarCliente(Long id, ClienteRequest request);

    Cliente ativarDesativarCliente(Long id);

    List<Cliente> listarClientesAtivos();

    List<Cliente> buscarClientesPorNome(String nome);

    List<Cliente> buscarClientesPorTelefone(String telefone);

    List<Cliente> buscarClientesPorEndereco(String endereco);

    Cliente inativarCliente(Long id);
    
    Cliente ativarCliente(Long id);

    List<Cliente> listarTodosClientes();
}
