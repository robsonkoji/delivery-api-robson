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
import com.deliverytech.delivery.metrics.PedidoMetrics;
import com.deliverytech.delivery.repository.*;
import com.deliverytech.delivery.security.SecurityUtils;
import com.deliverytech.delivery.service.PedidoService;
import com.deliverytech.delivery.service.UsuarioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Timer;


import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper mapper;

    private final PedidoMetrics pedidoMetrics;

    @Override
    public PedidoResponse criarPedido(PedidoRequest request) {
        Timer.Sample sample = pedidoMetrics.startTimer();

        try {
            Usuario usuario = usuarioService.getUsuarioLogado();

            if (!SecurityUtils.hasRole("ADMIN") && !request.getClienteId().equals(usuario.getId())) {
                throw new BusinessException("Você não tem permissão para criar pedidos para outros clientes.");
            }

            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

            if (!cliente.isAtivo()) {
                throw new BusinessException("Cliente está inativo");
            }

            Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

            if (Boolean.FALSE.equals(restaurante.getAtivo())) {
                throw new BusinessException("Restaurante está inativo");
            }

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setEnderecoEntrega(request.getEnderecoEntrega());
            pedido.setObservacoes(request.getObservacoes());
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setDataPedido(LocalDateTime.now());
            pedido.setTaxaEntrega(restaurante.getTaxaEntrega());

            var itens = request.getItens().stream().map(itemDto -> {
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

            pedidoMetrics.incrementarPedidosPorStatus(pedido.getStatus().name());

            return mapper.toResponse(salvo);

        } finally {
            pedidoMetrics.stopTimer(sample, "pedido.criacao.tempo", "criar");
        }
    }



    @Override
    public PedidoResponse buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

        if (!canAccess(id)) {
            throw new BusinessException("Você não tem permissão para visualizar este pedido.");
        }

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
        if (!canAccess(id)) {
            throw new BusinessException("Você não tem permissão para atualizar o status deste pedido.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

        if (isStatusFinal(pedido.getStatus())) {
            throw new TransactionException("Pedido não pode mais ser atualizado. Status atual: " + pedido.getStatus());
        }

        if (!isTransicaoValida(pedido.getStatus(), novoStatus)) {
            throw new BusinessException("Transição de status inválida: de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);

        pedidoMetrics.incrementarPedidosPorStatus(novoStatus.name());

        return mapper.toResponse(pedido);
    }

    /**
     * Verifica se a transição de status é válida.
     * Exemplo: De PENDENTE para CONFIRMADO é válido, mas de CANCELADO para ENTREGUE não é.
     */
    private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        // Exemplo de lógica básica de transição (ajuste conforme suas regras)
        switch (statusAtual) {
            case PENDENTE:
                return novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO:
                return novoStatus == StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO:
                return novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA:
                return novoStatus == StatusPedido.ENTREGUE;
            default:
                return false;
        }
    }


    /**
     * Verifica se o status atual é final e não pode ser alterado.
     */
    private boolean isStatusFinal(StatusPedido status) {
        return status == StatusPedido.CANCELADO || status == StatusPedido.ENTREGUE;
    }


    @Override
public void cancelarPedido(Long id) {
    if (!canAccess(id)) {
        throw new BusinessException("Você não tem permissão para cancelar este pedido.");
    }

    Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

    if (pedido.getStatus() != StatusPedido.CONFIRMADO) {
        throw new TransactionException("Apenas pedidos confirmados podem ser cancelados.");
    }

    pedido.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedido);
}


    @Override
    public List<PedidoResponse> listarPedidosComFiltro(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> (status == null || p.getStatus() == status))
                .filter(p -> (dataInicio == null || !p.getDataPedido().isBefore(dataInicio)))
                .filter(p -> (dataFim == null || !p.getDataPedido().isAfter(dataFim)))
                .toList();

        return pedidos.stream().map(mapper::toResponse).toList();
    }


    @Override
    public List<PedidoResponse> buscarPedidosPorRestaurante(Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        return pedidoRepository.findByRestauranteId(restaurante.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public boolean canAccess(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        Usuario usuario = usuarioService.getUsuarioLogado(); // método que você deve ter para pegar usuário logado

        return switch (usuario.getRole()) {
            case ADMIN -> true;

            case CLIENTE -> pedido.getCliente() != null &&
                        pedido.getCliente().getUsuario().getId().equals(usuario.getId());

            case RESTAURANTE -> pedido.getRestaurante() != null &&
                            pedido.getRestaurante().getUsuario().getId().equals(usuario.getId());

            default -> false;
        };
    }

   @Override
    @Timed(
        value = "pedidos.calculo.total",
        description = "Tempo para calcular o total do pedido"
    )
    public BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itens) {
        if (itens == null || itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return itens.stream()
            .filter(item -> item.getPrecoUnitario() != null && item.getQuantidade() != null)
            .map(item -> item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
