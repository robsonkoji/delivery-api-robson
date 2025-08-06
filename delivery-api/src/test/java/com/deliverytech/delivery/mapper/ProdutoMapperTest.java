package com.deliverytech.delivery.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProdutoMapperTest {

    private ProdutoMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ProdutoMapper();
    }

    @Test
    void deveMapearProdutoRequestParaProduto() {
        // Criar restaurante fake para associação
        Restaurante restaurante = new Restaurante();
        restaurante.setId(10L);
        restaurante.setNome("Restaurante Teste");

        ProdutoRequest request = new ProdutoRequest();
        request.setNome("X-Burguer");
        request.setDescricao("Delicioso hambúrguer");
        request.setPreco( new BigDecimal(15.50));
        request.setCategoria("Lanches");
        request.setDisponivel(true);
        request.setEstoque(100);

        Produto produto = mapper.toEntity(request, restaurante);

        assertThat(produto).isNotNull();
        assertThat(produto.getNome()).isEqualTo(request.getNome());
        assertThat(produto.getDescricao()).isEqualTo(request.getDescricao());
        assertThat(produto.getPreco()).isEqualTo(request.getPreco());
        assertThat(produto.getCategoria()).isEqualTo(request.getCategoria());
        assertThat(produto.getAtivo()).isEqualTo(request.getDisponivel());
        assertThat(produto.getEstoque()).isEqualTo(request.getEstoque());
        assertThat(produto.getRestaurante()).isEqualTo(restaurante);
    }

    @Test
    void deveMapearProdutoParaProdutoResponse() {
        Restaurante restaurante = new Restaurante();
        restaurante.setId(10L);
        restaurante.setNome("Restaurante Teste");

        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("X-Burguer");
        produto.setDescricao("Delicioso hambúrguer");
        produto.setPreco(new BigDecimal(15.50));
        produto.setCategoria("Lanches");
        produto.setAtivo(true);
        produto.setEstoque(100);
        produto.setRestaurante(restaurante);

        ProdutoResponse response = mapper.toResponse(produto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(produto.getId());
        assertThat(response.getNome()).isEqualTo(produto.getNome());
        assertThat(response.getDescricao()).isEqualTo(produto.getDescricao());
        assertThat(response.getPreco()).isEqualTo(produto.getPreco());
        assertThat(response.getCategoria()).isEqualTo(produto.getCategoria());
        assertThat(response.isDisponivel()).isEqualTo(produto.getAtivo());
        assertThat(response.getEstoque()).isEqualTo(produto.getEstoque());
        assertThat(response.getRestauranteId()).isEqualTo(restaurante.getId());
    }

    @Test
    void deveMapearProdutoParaProdutoResponseSemRestaurante() {
        Produto produto = new Produto();
        produto.setId(2L);
        produto.setNome("X-Salada");
        produto.setDescricao("Hambúrguer com salada");
        produto.setPreco(new BigDecimal(18.00));
        produto.setCategoria("Lanches");
        produto.setAtivo(false);
        produto.setEstoque(50);
        produto.setRestaurante(null); // sem restaurante

        ProdutoResponse response = mapper.toResponse(produto);

        assertThat(response).isNotNull();
        assertThat(response.getRestauranteId()).isNull();
    }
}
