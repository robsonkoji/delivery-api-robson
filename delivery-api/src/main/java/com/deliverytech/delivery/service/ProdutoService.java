package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;


import java.util.List;


public interface ProdutoService {

    ProdutoResponse cadastrarProduto(ProdutoRequest request);

    List<ProdutoResponse> buscarProdutosPorRestaurante(Long restauranteId);

    ProdutoResponse buscarProdutoPorId(Long id);

    ProdutoResponse atualizarProduto(Long id, ProdutoRequest request);

    ProdutoResponse alterarDisponibilidade(Long id, boolean disponivel);

    List<ProdutoResponse> buscarProdutosPorCategoria(String categoria);

    ProdutoResponse removerProduto(Long id);

    List<ProdutoResponse> buscarProdutosPorNome(String nome);

    boolean isOwner(Long produtoId);

}