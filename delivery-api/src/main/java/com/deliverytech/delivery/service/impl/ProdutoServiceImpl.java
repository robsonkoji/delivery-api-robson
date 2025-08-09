package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.mapper.ProdutoMapper;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.security.SecurityUtils;
import com.deliverytech.delivery.service.ProdutoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoServiceImpl.class);

    private final ProdutoRepository produtoRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoMapper mapper;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("delivery-api");

    public ProdutoServiceImpl(ProdutoRepository produtoRepository,
                              RestauranteRepository restauranteRepository,
                              ProdutoMapper mapper) {
        this.produtoRepository = produtoRepository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    private void logError(String msg, Exception e) {
        logger.error("[{}] {}", getCorrelationId(), msg, e);
    }

    @Override
    @CachePut(value = "produtos", key = "#result.id")
    public ProdutoResponse cadastrarProduto(ProdutoRequest request) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.cadastrarProduto").startSpan();
        span.setAttribute("restauranteId", request.getRestauranteId());

        logger.info("[{}] Tentando cadastrar produto para restauranteId={}", getCorrelationId(), request.getRestauranteId());
        try {
            Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

            if (!SecurityUtils.hasRole("ADMIN") &&
                !restaurante.getUsuario().getId().equals(SecurityUtils.getCurrentUserId())) {
                logger.warn("[{}] Acesso negado para cadastrar produto no restauranteId={} pelo usuárioId={}",
                        getCorrelationId(), request.getRestauranteId(), SecurityUtils.getCurrentUserId());
                span.addEvent("Acesso negado para cadastrar produto");
                throw new BusinessException("Acesso negado: você não tem permissão para cadastrar produto neste restaurante.");
            }

            Produto produto = mapper.toEntity(request, restaurante);
            Produto salvo = produtoRepository.save(produto);
            logger.info("[{}] Produto cadastrado com sucesso, id={}", getCorrelationId(), salvo.getId());
            return mapper.toResponse(salvo);

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            String msg = "Erro ao cadastrar produto para restauranteId=" + request.getRestauranteId();
            logError(msg, e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    @Cacheable(value = "produtos", key = "#restauranteId")
    public List<ProdutoResponse> buscarProdutosPorRestaurante(Long restauranteId) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.buscarProdutosPorRestaurante").startSpan();
        span.setAttribute("restauranteId", restauranteId);

        logger.debug("[{}] Buscando produtos disponíveis para restauranteId={}", getCorrelationId(), restauranteId);
        try {
            return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        } finally {
            span.end();
        }
    }

    @Override
    @Cacheable(value = "produtos", key = "#id")
    public ProdutoResponse buscarProdutoPorId(Long id) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.buscarProdutoPorId").startSpan();
        span.setAttribute("produtoId", id);

        logger.debug("[{}] Buscando produto por id={}", getCorrelationId(), id);
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("[{}] Produto não encontrado para id={}", getCorrelationId(), id);
                        return new EntityNotFoundException("Produto não encontrado");
                    });

            if (Boolean.FALSE.equals(produto.getDisponivel())) {
                logger.warn("[{}] Produto id={} não está disponível", getCorrelationId(), id);
                span.addEvent("Produto não disponível");
                throw new BusinessException("Produto não disponível");
            }

            return mapper.toResponse(produto);
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    @CachePut(value = "produtos", key = "#id")
    public ProdutoResponse atualizarProduto(Long id, ProdutoRequest request) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.atualizarProduto").startSpan();
        span.setAttribute("produtoId", id);
        span.setAttribute("restauranteId", request.getRestauranteId());

        logger.info("[{}] Atualizando produto id={}", getCorrelationId(), id);
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                    .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

            if (!SecurityUtils.hasRole("ADMIN") &&
                !restaurante.getUsuario().getId().equals(SecurityUtils.getCurrentUserId())) {
                logger.warn("[{}] Acesso negado para atualizar produto id={} pelo usuárioId={}",
                        getCorrelationId(), id, SecurityUtils.getCurrentUserId());
                span.addEvent("Acesso negado para atualizar produto");
                throw new BusinessException("Acesso negado: você não tem permissão para atualizar este produto.");
            }

            produto.setNome(request.getNome());
            produto.setDescricao(request.getDescricao());
            produto.setPreco(request.getPreco());
            produto.setCategoria(request.getCategoria());
            produto.setRestaurante(restaurante);

            Produto salvo = produtoRepository.save(produto);
            logger.info("[{}] Produto atualizado com sucesso, id={}", getCorrelationId(), salvo.getId());
            return mapper.toResponse(salvo);

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            String msg = "Erro ao atualizar produto id=" + id;
            logError(msg, e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public ProdutoResponse alterarDisponibilidade(Long id, boolean disponivel) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.alterarDisponibilidade").startSpan();
        span.setAttribute("produtoId", id);
        span.setAttribute("disponivel", disponivel);

        logger.info("[{}] Alterando disponibilidade do produto id={} para {}", getCorrelationId(), id, disponivel);
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("[{}] Produto não encontrado para alterar disponibilidade, id={}", getCorrelationId(), id);
                        return new EntityNotFoundException("Produto não encontrado");
                    });

            produto.setDisponivel(disponivel);
            produtoRepository.save(produto);
            logger.info("[{}] Disponibilidade alterada com sucesso para produto id={}", getCorrelationId(), id);
            return mapper.toResponse(produto);

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    @Cacheable(value = "produtos", key = "#categoria")
    public List<ProdutoResponse> buscarProdutosPorCategoria(String categoria) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.buscarProdutosPorCategoria").startSpan();
        span.setAttribute("categoria", categoria);

        logger.debug("[{}] Buscando produtos disponíveis para categoria '{}'", getCorrelationId(), categoria);
        try {
            return produtoRepository.findByCategoriaAndDisponivelTrue(categoria)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        } finally {
            span.end();
        }
    }

    @Override
    @CacheEvict(value = "produtos", key = "#id")
    public ProdutoResponse removerProduto(Long id) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.removerProduto").startSpan();
        span.setAttribute("produtoId", id);

        logger.info("[{}] Removendo produto id={}", getCorrelationId(), id);
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("[{}] Produto não encontrado para remoção, id={}", getCorrelationId(), id);
                        return new EntityNotFoundException("Produto não encontrado");
                    });

            ProdutoResponse response = mapper.toResponse(produto);
            produtoRepository.delete(produto);
            logger.info("[{}] Produto removido com sucesso, id={}", getCorrelationId(), id);
            return response;

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    @Cacheable(value = "produtos", key = "#nome")
    public List<ProdutoResponse> buscarProdutosPorNome(String nome) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.buscarProdutosPorNome").startSpan();
        span.setAttribute("nomeBusca", nome);

        logger.debug("[{}] Buscando produtos por nome contendo '{}'", getCorrelationId(), nome);
        try {
            List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome);
            return produtos.stream()
                    .map(mapper::toResponse)
                    .toList();
        } finally {
            span.end();
        }
    }

    @Override
    public boolean isOwner(Long produtoId) {
        Span span = tracer.spanBuilder("ProdutoServiceImpl.isOwner").startSpan();
        span.setAttribute("produtoId", produtoId);

        logger.debug("[{}] Verificando se usuário atual é dono do produto id={}", getCorrelationId(), produtoId);
        try {
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> {
                        logger.error("[{}] Produto não encontrado para verificação de dono, id={}", getCorrelationId(), produtoId);
                        return new EntityNotFoundException("Produto não encontrado");
                    });

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof Usuario usuario) {
                Restaurante restaurante = produto.getRestaurante();
                boolean isOwner = restaurante.getUsuario().getId().equals(usuario.getId());
                logger.debug("[{}] Usuário id={} é dono do produto? {}", getCorrelationId(), usuario.getId(), isOwner);
                return isOwner;
            }

            logger.warn("[{}] Usuário não autenticado ou tipo do principal inesperado", getCorrelationId());
            return false;

        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }
}
