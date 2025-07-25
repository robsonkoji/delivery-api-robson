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
}
