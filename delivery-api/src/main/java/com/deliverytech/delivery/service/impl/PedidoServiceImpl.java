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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final UsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoMapper mapper;
    private final PedidoMetrics pedidoMetrics;

    // OpenTelemetry Tracer
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("delivery-api");

    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    private RuntimeException logAndWrap(String msg, Exception ex) {
        logger.error("[{}] {}", getCorrelationId(), msg, ex);
        return new RuntimeException(msg, ex);
    }

    @Override
    public PedidoResponse criarPedido(PedidoRequest request) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.criarPedido").startSpan();
        Timer.Sample sample = pedidoMetrics.startTimer();

        try {
            span.setAttribute("clienteId", request.getClienteId());
            span.setAttribute("restauranteId", request.getRestauranteId());
            span.setAttribute("qtdItens", request.getItens() != null ? request.getItens().size() : 0);

            Usuario usuario = usuarioService.getUsuarioLogado();

            if (!SecurityUtils.hasRole("ADMIN") && !request.getClienteId().equals(usuario.getId())) {
                span.addEvent("Permissão negada");
                throw new BusinessException("Você não tem permissão para criar pedidos para outros clientes.");
            }

            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

            if (!cliente.isAtivo()) {
                span.addEvent("Cliente inativo");
                throw new BusinessException("Cliente está inativo");
            }

            Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

            if (Boolean.FALSE.equals(restaurante.getAtivo())) {
                span.addEvent("Restaurante inativo");
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

            var itens = request.getItens().stream()
                .map(itemDto -> {
                    Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

                    if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                        throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante informado");
                    }

                    if (Boolean.FALSE.equals(produto.getDisponivel())) {
                        throw new BusinessException("Produto indisponível: " + produto.getNome());
                    }

                    return new ItemPedido(produto, itemDto.getQuantidade());
                })
                .toList();

            itens.forEach(pedido::adicionarItem);
            pedido.calcularTotais();

            Pedido salvo = pedidoRepository.save(pedido);

            pedidoMetrics.incrementarPedidosPorStatus(pedido.getStatus().name());

            logger.info("[{}] Pedido criado: id={}, clienteId={}, restauranteId={}",
                    getCorrelationId(), salvo.getId(), cliente.getId(), restaurante.getId());

            return mapper.toResponse(salvo);

        } catch (BusinessException | EntityNotFoundException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao criar pedido", ex);
        } finally {
            pedidoMetrics.stopTimer(sample, "pedido.criacao.tempo", "criar");
            span.end();
        }
    }

    @Override
    public PedidoResponse buscarPedidoPorId(Long id) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.buscarPedidoPorId").startSpan();
        span.setAttribute("pedidoId", id);

        try {
            Pedido pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

            if (!canAccess(id)) {
                span.addEvent("Acesso negado");
                throw new BusinessException("Você não tem permissão para visualizar este pedido.");
            }

            logger.debug("[{}] Pedido encontrado: id={}", getCorrelationId(), id);

            return mapper.toResponse(pedido);

        } catch (BusinessException | EntityNotFoundException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao buscar pedido por id " + id, ex);
        } finally {
            span.end();
        }
    }

    @Override
    public List<PedidoResponse> buscarPedidosPorCliente(Long clienteId) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.buscarPedidosPorCliente").startSpan();
        span.setAttribute("clienteId", clienteId);

        try {
            return pedidoRepository.findByClienteId(clienteId).stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao buscar pedidos por cliente " + clienteId, ex);
        } finally {
            span.end();
        }
    }

    @Override
    public PedidoResponse atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.atualizarStatusPedido").startSpan();
        span.setAttribute("pedidoId", id);
        span.setAttribute("novoStatus", novoStatus.name());

        try {
            if (!canAccess(id)) {
                span.addEvent("Acesso negado");
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

            logger.info("[{}] Status do pedido atualizado: id={}, novoStatus={}", getCorrelationId(), id, novoStatus);

            return mapper.toResponse(pedido);

        } catch (BusinessException | EntityNotFoundException | TransactionException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao atualizar status do pedido id " + id, ex);
        } finally {
            span.end();
        }
    }

    private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case PENDENTE -> novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO -> novoStatus == StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO -> novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA -> novoStatus == StatusPedido.ENTREGUE;
            default -> false;
        };
    }

    private boolean isStatusFinal(StatusPedido status) {
        return status == StatusPedido.CANCELADO || status == StatusPedido.ENTREGUE;
    }

    @Override
    public void cancelarPedido(Long id) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.cancelarPedido").startSpan();
        span.setAttribute("pedidoId", id);

        try {
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

            logger.info("[{}] Pedido cancelado: id={}", getCorrelationId(), id);

        } catch (BusinessException | EntityNotFoundException | TransactionException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao cancelar pedido id " + id, ex);
        } finally {
            span.end();
        }
    }

    @Override
    public List<PedidoResponse> listarPedidosComFiltro(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.listarPedidosComFiltro").startSpan();
        span.setAttribute("status", status != null ? status.name() : "TODOS");

        try {
            List<Pedido> pedidos = pedidoRepository.findAll().stream()
                    .filter(p -> (status == null || p.getStatus() == status))
                    .filter(p -> (dataInicio == null || !p.getDataPedido().isBefore(dataInicio)))
                    .filter(p -> (dataFim == null || !p.getDataPedido().isAfter(dataFim)))
                    .toList();

            return pedidos.stream().map(mapper::toResponse).toList();

        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao listar pedidos com filtro", ex);
        } finally {
            span.end();
        }
    }

    @Override
    public List<PedidoResponse> buscarPedidosPorRestaurante(Long restauranteId) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.buscarPedidosPorRestaurante").startSpan();
        span.setAttribute("restauranteId", restauranteId);

        try {
            Restaurante restaurante = restauranteRepository.findById(restauranteId)
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

            return pedidoRepository.findByRestauranteId(restaurante.getId())
                    .stream()
                    .map(mapper::toResponse)
                    .toList();

        } catch (EntityNotFoundException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao buscar pedidos por restaurante " + restauranteId, ex);
        } finally {
            span.end();
        }
    }

    @Override
    public boolean canAccess(Long pedidoId) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.canAccess").startSpan();
        span.setAttribute("pedidoId", pedidoId);

        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

            Usuario usuario = usuarioService.getUsuarioLogado();

            return switch (usuario.getRole()) {
                case ADMIN -> true;
                case CLIENTE -> pedido.getCliente() != null &&
                        pedido.getCliente().getUsuario().getId().equals(usuario.getId());
                case RESTAURANTE -> pedido.getRestaurante() != null &&
                        pedido.getRestaurante().getUsuario().getId().equals(usuario.getId());
                default -> false;
            };
        } catch (EntityNotFoundException ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw ex;
        } catch (Exception ex) {
            span.recordException(ex);
            span.setAttribute("error", true);
            throw logAndWrap("Erro ao verificar acesso ao pedido " + pedidoId, ex);
        } finally {
            span.end();
        }
    }

    @Override
    @Timed(
        value = "pedidos.calculo.total",
        description = "Tempo para calcular o total do pedido"
    )
    public BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itens) {
        Span span = tracer.spanBuilder("PedidoServiceImpl.calcularTotalPedido").startSpan();
        span.setAttribute("qtdItens", itens != null ? itens.size() : 0);

        try {
            if (itens == null || itens.isEmpty()) {
                return BigDecimal.ZERO;
            }

            return itens.stream()
                .filter(item -> item.getPrecoUnitario() != null && item.getQuantidade() != null)
                .map(item -> item.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            span.end();
        }
    }
}
