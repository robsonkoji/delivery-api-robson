package com.deliverytech.delivery.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;  // Para correlação de logs
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.exception.ValidationException;
import com.deliverytech.delivery.mapper.RestauranteMapper;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.security.SecurityUtils;
import com.deliverytech.delivery.service.AuthService;
import com.deliverytech.delivery.service.RestauranteService;

import io.opentelemetry.api.GlobalOpenTelemetry;  // Para tracing distribuído
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  // Injeta automaticamente os campos finais pelo construtor
public class RestauranteServiceImpl implements RestauranteService {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteServiceImpl.class);

    private final AuthService authService;
    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper mapper;

    // Objeto para rastreamento distribuído via OpenTelemetry
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("delivery-api");

    /**
     * Recupera o correlationId do MDC para logs (ou "N/A" se não existir)
     * Isso ajuda a correlacionar logs em sistemas distribuídos.
     */
    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    /**
     * Cadastra um novo restaurante e atualiza o cache "restaurantes" com a chave do id do restaurante criado.
     * Usa @CachePut porque a operação grava no banco e no cache.
     */
    @Override
    @CachePut(value = "restaurantes", key = "#result.id")
    public RestauranteResponse cadastrarRestaurante(RestauranteRequest request) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.cadastrarRestaurante").startSpan();
        try {
            Restaurante restaurante = mapper.toEntity(request);
            restaurante.setAtivo(true);
            Restaurante salvo = restauranteRepository.save(restaurante);
            logger.info("[{}] Restaurante cadastrado com sucesso: id={}", getCorrelationId(), salvo.getId());
            return mapper.toResponse(salvo);
        } catch (Exception e) {
            // Registra exceção no tracing e loga erro
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao cadastrar restaurante", getCorrelationId(), e);
            throw new RuntimeException("Erro ao cadastrar restaurante", e);
        } finally {
            span.end();
        }
    }

    /**
     * Busca um restaurante por ID, usando cache "restaurantes" com chave id.
     * Caso não esteja no cache, busca no banco e popula cache.
     */
    @Override
    @Cacheable(value = "restaurantes", key = "#id")
    public RestauranteResponse buscarRestaurantePorId(Long id) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.buscarRestaurantePorId").startSpan();
        span.setAttribute("restauranteId", id);
        try {
            Restaurante restaurante = buscarOuLancar(id);
            return mapper.toResponse(restaurante);
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para id={}", getCorrelationId(), id);
            throw e;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao buscar restaurante por id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao buscar restaurante", e);
        } finally {
            span.end();
        }
    }

    /**
     * Atualiza dados do restaurante e atualiza o cache com chave id.
     * Valida permissão: só ADMIN ou dono pode atualizar.
     */
    @Override
    @CachePut(value = "restaurantes", key = "#id")
    public RestauranteResponse atualizarRestaurante(Long id, RestauranteRequest request) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.atualizarRestaurante").startSpan();
        span.setAttribute("restauranteId", id);
        authService.getUsuarioAutenticado();

        if (!SecurityUtils.hasRole("ADMIN") && !isOwner(id)) {
            String msg = "Você não tem permissão para atualizar este restaurante id=" + id;
            logger.warn("[{}] {}", getCorrelationId(), msg);
            throw new ValidationException(msg);
        }

        try {
            Restaurante restaurante = buscarOuLancar(id);
            // Atualiza campos
            restaurante.setNome(request.getNome());
            restaurante.setCategoria(request.getCategoria());
            restaurante.setEndereco(request.getEndereco());
            restaurante.setTelefone(request.getTelefone());
            restaurante.setTaxaEntrega(request.getTaxaEntrega());

            Restaurante salvo = restauranteRepository.save(restaurante);
            logger.info("[{}] Restaurante atualizado com sucesso: id={}", getCorrelationId(), id);
            return mapper.toResponse(salvo);
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para atualização: id={}", getCorrelationId(), id);
            throw e;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao atualizar restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao atualizar restaurante", e);
        } finally {
            span.end();
        }
    }

    /**
     * Busca restaurantes por categoria (cacheada pela categoria).
     */
    @Override
    @Cacheable(value = "restaurantes", key = "#categoria")
    public List<RestauranteResponse> buscarRestaurantesPorCategoria(String categoria) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.buscarRestaurantesPorCategoria").startSpan();
        span.setAttribute("categoria", categoria);
        try {
            List<Restaurante> restaurantes = restauranteRepository.findByCategoriaIgnoreCase(categoria);
            return restaurantes.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao buscar restaurantes por categoria={}", getCorrelationId(), categoria, e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    /**
     * Busca restaurantes ativos (disponíveis).
     * Cacheada sob a chave fixa 'disponiveis'.
     */
    @Override
    @Cacheable(value = "restaurantes", key = "'disponiveis'")
    public List<RestauranteResponse> buscarRestaurantesDisponiveis() {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.buscarRestaurantesDisponiveis").startSpan();
        try {
            List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();
            return restaurantes.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao buscar restaurantes disponíveis", getCorrelationId(), e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    /**
     * Busca restaurantes com filtros opcionais de categoria e ativo.
     * Cacheada com chave composta.
     */
    @Override
    @Cacheable(value = "restaurantes", key = "{#categoria != null ? #categoria : 'all', #ativo != null ? #ativo.toString() : 'all'}")
    public List<RestauranteResponse> buscarRestaurantesComFiltros(String categoria, Boolean ativo) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.buscarRestaurantesComFiltros").startSpan();
        span.setAttribute("categoria", categoria == null ? "null" : categoria);
        span.setAttribute("ativo", ativo == null ? "null" : ativo.toString());
        try {
            List<Restaurante> restaurantes;
            if (categoria != null && ativo != null) {
                restaurantes = restauranteRepository.findByCategoriaIgnoreCaseAndAtivo(categoria, ativo);
            } else if (categoria != null) {
                restaurantes = restauranteRepository.findByCategoriaIgnoreCase(categoria);
            } else if (ativo != null) {
                restaurantes = restauranteRepository.findByAtivo(ativo);
            } else {
                restaurantes = restauranteRepository.findAll();
            }
            return restaurantes.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao buscar restaurantes com filtros: categoria={}, ativo={}", getCorrelationId(), categoria, ativo, e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    /**
     * Alterna status ativo/inativo do restaurante.
     * Evita cache desatualizado removendo a entrada da cache após alteração.
     * Valida permissão: só ADMIN ou dono pode alterar.
     */
    @Override
    @CacheEvict(value = "restaurantes", key = "#id")
    public RestauranteResponse alterarStatusRestaurante(Long id) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.alterarStatusRestaurante").startSpan();
        span.setAttribute("restauranteId", id);
        authService.getUsuarioAutenticado();

        if (!SecurityUtils.hasRole("ADMIN") && !isOwner(id)) {
            String msg = "Você não tem permissão para alterar o status do restaurante id=" + id;
            logger.warn("[{}] {}", getCorrelationId(), msg);
            throw new ValidationException(msg);
        }

        try {
            Restaurante restaurante = buscarOuLancar(id);
            restaurante.setAtivo(!restaurante.getAtivo());
            Restaurante salvo = restauranteRepository.save(restaurante);
            logger.info("[{}] Status do restaurante alterado: id={}, ativo={}", getCorrelationId(), id, salvo.getAtivo());
            return mapper.toResponse(salvo);
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para alterar status: id={}", getCorrelationId(), id);
            throw e;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao alterar status do restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao alterar status do restaurante", e);
        } finally {
            span.end();
        }
    }

    /**
     * Calcula taxa de entrega com regra simples: se CEP começa com "01", taxa é reduzida à metade.
     * Caso contrário, retorna taxa original.
     */
    @Override
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.calcularTaxaEntrega").startSpan();
        span.setAttribute("restauranteId", restauranteId);
        span.setAttribute("cep", cep == null ? "null" : cep);
        try {
            Restaurante restaurante = buscarOuLancar(restauranteId);
            if (cep != null && cep.startsWith("01")) {
                return restaurante.getTaxaEntrega().multiply(BigDecimal.valueOf(0.5));
            }
            return restaurante.getTaxaEntrega();
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para calcular taxa: id={}", getCorrelationId(), restauranteId);
            throw e;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao calcular taxa de entrega para restaurante id={}", getCorrelationId(), restauranteId, e);
            throw new RuntimeException("Erro ao calcular taxa de entrega", e);
        } finally {
            span.end();
        }
    }

    /**
     * Busca restaurantes próximos por prefixo do CEP (3 primeiros caracteres do CEP).
     * Filtra restaurantes cujo endereço contenha esse prefixo.
     */
    @Override
    public List<RestauranteResponse> buscarRestaurantesProximos(String cep) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.buscarRestaurantesProximos").startSpan();
        span.setAttribute("cep", cep == null ? "null" : cep);
        try {
            if (cep == null || cep.length() < 3) {
                logger.warn("[{}] CEP inválido para busca de restaurantes próximos: {}", getCorrelationId(), cep);
                return Collections.emptyList();
            }
            String bairroPrefix = cep.substring(0, 3);
            List<Restaurante> proximos = restauranteRepository.findAll().stream()
                    .filter(r -> r.getEndereco() != null && r.getEndereco().contains(bairroPrefix))
                    .toList();

            return proximos.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao buscar restaurantes próximos para cep={}", getCorrelationId(), cep, e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    /**
     * Método auxiliar para buscar restaurante ou lançar exceção se não encontrado.
     */
    private Restaurante buscarOuLancar(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
    }

    /**
     * Verifica se o usuário autenticado é o dono do restaurante.
     */
    @Override
    public boolean isOwner(Long restauranteId) {
        Restaurante restaurante = buscarOuLancar(restauranteId);
        Usuario logado = authService.getUsuarioAutenticado();
        return restaurante.getUsuario() != null && restaurante.getUsuario().getId().equals(logado.getId());
    }

    /**
     * Remove restaurante, limpando a cache da chave específica após remoção.
     * Valida permissão: só ADMIN ou dono pode remover.
     */
    @Override
    @CacheEvict(value = "restaurantes", key = "#id")
    public void removerRestaurante(Long id) {
        Span span = tracer.spanBuilder("RestauranteServiceImpl.removerRestaurante").startSpan();
        span.setAttribute("restauranteId", id);
        authService.getUsuarioAutenticado();

        if (!SecurityUtils.hasRole("ADMIN") && !isOwner(id)) {
            String msg = "Você não tem permissão para remover o restaurante id=" + id;
            logger.warn("[{}] {}", getCorrelationId(), msg);
            throw new ValidationException(msg);
        }

        try {
            Restaurante restaurante = buscarOuLancar(id);
            restauranteRepository.delete(restaurante);
            logger.info("[{}] Restaurante removido com sucesso: id={}", getCorrelationId(), id);
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para remoção: id={}", getCorrelationId(), id);
            throw e;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logger.error("[{}] Erro ao remover restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao remover restaurante", e);
        } finally {
            span.end();
        }
    }
}
