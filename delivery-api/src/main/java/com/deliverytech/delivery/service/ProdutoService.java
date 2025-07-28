package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;


import java.util.List;


public interface ProdutoService {

    ProdutoResponse cadastrarProduto(ProdutoRequest request);

    List<ProdutoResponse> buscarProdutosPorRestaurante(Long restauranteId);

    ProdutoResponse buscarProdutoPorId(Long id);

    ProdutoResponse atualizarProduto(Long id, ProdutoRequest request);

    void alterarDisponibilidade(Long id, boolean disponivel);

    List<ProdutoResponse> buscarProdutosPorCategoria(String categoria);

}