package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.exception.TransactionException;
import com.deliverytech.delivery.mapper.PedidoMapper;
import com.deliverytech.delivery.repository.*;
import com.deliverytech.delivery.service.PedidoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper mapper;

    @Override
    public PedidoResponse criarPedido(PedidoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        if (!cliente.isAtivo()) {
            throw new BusinessException("Cliente está inativo");
        }

        Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        if (!restaurante.isAtivo()) {
            throw new BusinessException("Restaurante está inativo");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setEnderecoEntrega(request.getEnderecoEntrega());
        pedido.setObservacoes(request.getObservacoes());
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setTaxaEntrega(restaurante.getTaxaEntrega());

        List<ItemPedido> itens = request.getItens().stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante informado");
            }

            if (Boolean.FALSE.equals(produto.getDisponivel())) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            return new ItemPedido(produto, itemDto.getQuantidade());
        }).toList();

        itens.forEach(pedido::adicionarItem);
        pedido.calcularTotais();

        Pedido salvo = pedidoRepository.save(pedido);
        return mapper.toResponse(salvo);
    }

    @Override
    public PedidoResponse buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        return mapper.toResponse(pedido);
    }

    @Override
    public List<PedidoResponse> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PedidoResponse atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new TransactionException("Pedido não pode mais ser atualizado");
        }

        pedido.setStatus(novoStatus);
        return mapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itensRequest) {
        return itensRequest.stream().map(itemDto -> {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado"));
            return produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.CRIADO) {
            throw new TransactionException("Apenas pedidos criados podem ser cancelados.");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}
