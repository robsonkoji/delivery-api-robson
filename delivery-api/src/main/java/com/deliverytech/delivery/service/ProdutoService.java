package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.entity.Produto;

import java.util.List;


public interface ProdutoService {

    Produto cadastrarProduto(ProdutoRequest request);

    List<Produto> buscarProdutosPorRestaurante(Long restauranteId);

    Produto buscarProdutoPorId(Long id);

    Produto atualizarProduto(Long id, ProdutoRequest request);

    void alterarDisponibilidade(Long id, boolean disponivel);

    List<Produto> buscarProdutosPorCategoria(String categoria);

}