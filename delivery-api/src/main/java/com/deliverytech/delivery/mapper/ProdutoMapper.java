package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;


import lombok.*;

import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class ProdutoMapper {

    public Produto toEntity(ProdutoRequest request, Restaurante restaurante) {
        Produto produto = new Produto();
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setAtivo(request.getDisponivel());
        produto.setRestaurante(restaurante);
        return produto;
    }

    public ProdutoResponse toResponse(Produto produto) {
        ProdutoResponse response = new ProdutoResponse();
        response.setId(produto.getId());
        response.setNome(produto.getNome());
        response.setDescricao(produto.getDescricao());
        response.setPreco(produto.getPreco());
        response.setCategoria(produto.getCategoria());
        response.setDisponivel(produto.getAtivo());
        if (produto.getRestaurante() != null) {
            response.setRestauranteId(produto.getRestaurante().getId());
        }
        return response;
    }

    
}
