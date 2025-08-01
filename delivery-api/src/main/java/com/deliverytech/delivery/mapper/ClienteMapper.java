package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.dto.response.ClienteResponse;
import com.deliverytech.delivery.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail().toLowerCase().trim());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(request.getEndereco());
        return cliente;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNome(cliente.getNome());
        response.setEmail(cliente.getEmail());
        response.setTelefone(cliente.getTelefone());
        response.setEndereco(cliente.getEndereco());
        response.setAtivo(cliente.isAtivo());
        response.setDataCriacao(cliente.getDataCriacao());
        return response;
    }
}

