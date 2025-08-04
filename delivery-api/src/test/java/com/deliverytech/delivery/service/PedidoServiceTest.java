package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.dto.response.ClienteResponse;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.GlobalExceptionHandler;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Produto produto;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Robson");

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Pizza Tech");

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Pizza Calabresa");
        produto.setPreco(new BigDecimal("50.00"));
        produto.setEstoque(10);
        produto.setRestaurante(restaurante);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        PedidoRequest pedidoRequest = new PedidoRequest();

// Convertendo Map para List<ItemPedidoRequest>
    Map<Long, Integer> itensMap = Map.of(1L, 2);
    List<ItemPedidoRequest> itens = itensMap.entrySet().stream()
        .map(entry -> {
            ItemPedidoRequest item = new ItemPedidoRequest();
            item.setProdutoId(entry.getKey());
            item.setQuantidade(entry.getValue());
            return item;
        })
        .toList();

    pedidoRequest.setItens(itens);
    pedidoRequest.setEnderecoEntrega("Rua Exemplo, 123");

    Pedido pedido = pedidoService.criarPedido(pedidoRequest);

    assertNotNull(pedido);
    assertEquals(StatusPedido.CRIADO, pedido.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setItens(Map.of(999L, 1));

        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
            () -> pedidoService.criarPedido(request, cliente));

        verify(produtoRepository).findById(999L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        produto.setEstoque(1); // Estoque insuficiente

        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setItens(Map.of(produto.getId(), 5)); // pedido maior que estoque

        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> pedidoService.criarPedido(request, cliente));

        assertEquals("Estoque insuficiente para o produto: Pizza Calabresa", exception.getMessage());

        verify(produtoRepository).findById(produto.getId());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveAtualizarStatusPedido() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus(StatusPedido.CRIADO);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido atualizado = pedidoService.atualizarStatusPedido(1L, StatusPedido.PREPARANDO);

        assertEquals(StatusPedido.A_CAMINHO, atualizado.getStatus());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveLancarExcecaoAoAtualizarPedidoInexistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
            () -> pedidoService.atualizarStatusPedido(1L, StatusPedido.ENTREGUE));

        verify(pedidoRepository, never()).save(any());
    }

}
