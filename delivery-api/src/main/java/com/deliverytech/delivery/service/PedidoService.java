package com.deliverytech.delivery.service;

import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.deliverytech.delivery.enums.StatusPedido;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado: ";

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, RestauranteRepository restauranteRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Criar novo pedido
     */
    public Pedido criarPedido(Long clienteId, Long restauranteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));

        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        if (!cliente.isAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos");
        }

        if (!restaurante.isAtivo()) {
            throw new IllegalArgumentException("Restaurante não está disponível");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.PENDENTE);

        return pedidoRepository.save(pedido);
    }

    /**
     * Adicionar item ao pedido
     */
    public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = buscarPorId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException(PEDIDO_NAO_ENCONTRADO + pedidoId));

        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));

        if (!Boolean.TRUE.equals(produto.getDisponivel())) {
            throw new IllegalArgumentException("Produto não disponível: " + produto.getNome());
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        // Verificar se produto pertence ao mesmo restaurante do pedido
        if (!produto.getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
            throw new IllegalArgumentException("Produto não pertence ao restaurante do pedido");
        }

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        item.calcularSubtotal();

        pedido.adicionarItem(item);

        return pedidoRepository.save(pedido);
    }

    /**
     * Confirmar pedido
     */
    public Pedido confirmarPedido(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException(PEDIDO_NAO_ENCONTRADO + pedidoId));

        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalArgumentException("Apenas pedidos pendentes podem ser confirmados");
        }

        if (pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
        }

        pedido.confirmar();
        return pedidoRepository.save(pedido);
    }

    /**
     * Buscar por ID
     */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Listar pedidos por cliente
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
    }

    /**
     * Buscar por número do pedido
     */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorNumero(String numeroPedido) {
        return Optional.ofNullable(pedidoRepository.findByNumeroPedido(numeroPedido));
    }

    /**
     * Cancelar pedido
     */
    public Pedido cancelarPedido(Long pedidoId, String motivo) {
        Pedido pedido = buscarPorId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException(PEDIDO_NAO_ENCONTRADO + pedidoId));

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Pedido já entregue não pode ser cancelado");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido já está cancelado");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        if (motivo != null && !motivo.trim().isEmpty()) {
            pedido.setObservacoes(pedido.getObservacoes() + " | Cancelado: " + motivo);
        }

        return pedidoRepository.save(pedido);
    }

    /**
    * Atualizar status do pedido
    */
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
    Pedido pedido = buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException(PEDIDO_NAO_ENCONTRADO + pedidoId));

    pedido.setStatus(novoStatus);

    return pedidoRepository.save(pedido);
    }
}