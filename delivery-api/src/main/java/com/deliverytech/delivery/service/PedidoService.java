package com.deliverytech.delivery.service;

import com.deliverytech.delivery.model.*;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.enums.StatusPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
        PedidoRepository pedidoRepository,
        ClienteRepository clienteRepository,
        RestauranteRepository restauranteRepository,
        ProdutoRepository produtoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
    }

    /*
     * pedidos de um cliente
     */
    public List<Pedido> buscarPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }
        return pedidoRepository.findByClienteId(clienteId);
    }
    
    /**
     * Pedidos por status
     */
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        if (status == null) {
            throw new IllegalArgumentException("Status do pedido não pode ser nulo");
        }
        return pedidoRepository.findByStatus(status);
    }
    
    /**
     * Últimos 10 pedidos
     */
    public List<Pedido> buscarUltimos10Pedidos() {
        return pedidoRepository.findTop10ByOrderByDataPedidoDesc();
    }

    /**
     * Pedidos em um período
     */
    public List<Pedido> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Período não pode ser nulo");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data de início não pode ser após a data de fim");
        }
        return pedidoRepository.findByDataPedidoBetween(inicio, fim);
    }

    /**
     * Criar novo pedido
     */
    public Pedido criar(Pedido pedido) {
        validarPedido(pedido);

        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        Restaurante restaurante = restauranteRepository.findById(pedido.getRestaurante().getId())
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado"));

        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.CRIADO);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));

            if (!produto.isDisponivel()) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }

            item.setProduto(produto);
            item.setPedido(pedido);
            item.setPrecoUnitario(produto.getPreco());
            item.setPrecoTotal(produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())));

            subtotal = subtotal.add(item.getPrecoTotal());
        }

        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(BigDecimal.valueOf(5)); // fixo para exemplo
        pedido.setValorTotal(subtotal.add(pedido.getTaxaEntrega()));

        return pedidoRepository.save(pedido);
    }

    /**
     * Alterar status do pedido
     */
    public Pedido alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        if (!podeAlterarStatus(pedido.getStatus(), novoStatus)) {
    throw new IllegalArgumentException("Não é possível mudar de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    /**
     * Buscar por ID
     */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    private boolean podeAlterarStatus(StatusPedido atual, StatusPedido novo) {
    return switch (atual) {
        case PENDENTE -> novo == StatusPedido.CONFIRMADO || novo == StatusPedido.CANCELADO;
        case CONFIRMADO -> novo == StatusPedido.PREPARANDO;
        case PREPARANDO -> novo == StatusPedido.SAIU_PARA_ENTREGA;
        case SAIU_PARA_ENTREGA -> novo == StatusPedido.ENTREGUE;
        default -> false;
    };
}


    private void validarPedido(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        if (pedido.getRestaurante() == null || pedido.getRestaurante().getId() == null) {
            throw new IllegalArgumentException("Restaurante é obrigatório");
        }
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter ao menos um item");
        }
    }
}
