package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.mapper.ProdutoMapper;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.service.ProdutoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoMapper mapper;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository,
                               RestauranteRepository restauranteRepository,
                               ProdutoMapper mapper) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    @Override
    public ProdutoResponse cadastrarProduto(ProdutoRequest request) {
        Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        Produto produto = mapper.toEntity(request, restaurante);
        Produto salvo = produtoRepository.save(produto);
        return mapper.toResponse(salvo);
    }

    @Override
    public List<ProdutoResponse> buscarProdutosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProdutoResponse buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (Boolean.FALSE.equals(produto.getDisponivel())) {
            throw new BusinessException("Produto não disponível");
        }

        return mapper.toResponse(produto);
    }

    @Override
    public ProdutoResponse atualizarProduto(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setCategoria(request.getCategoria());
        produto.setRestaurante(restaurante);

        return mapper.toResponse(produtoRepository.save(produto));
    }

    @Override
    public ProdutoResponse alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);

        return mapper.toResponse(produto);
    }


    @Override
    public List<ProdutoResponse> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProdutoResponse removerProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        ProdutoResponse response = mapper.toResponse(produto); // converte para DTO antes de deletar
        produtoRepository.delete(produto);
        return response;
    }

    @Override
    public List<ProdutoResponse> buscarProdutosPorNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome);
        return produtos.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
