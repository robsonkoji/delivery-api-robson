package com.deliverytech.delivery.service;

import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;

    ProdutoService(ProdutoRepository produtoRepository, RestauranteRepository restauranteRepository) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    public Produto cadastrar(Produto produto, Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));
        validarDadosProduto(produto);

        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        return produtoRepository.save(produto);
    }


    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarPorPrecoMaximo(BigDecimal precoMax) {
        return produtoRepository.findByPrecoLessThanEqual(precoMax);
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        validarDadosProduto(produtoAtualizado);

        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setCategoria(produtoAtualizado.getCategoria());

        return produtoRepository.save(produto);
    }

    public void alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
    }
}
