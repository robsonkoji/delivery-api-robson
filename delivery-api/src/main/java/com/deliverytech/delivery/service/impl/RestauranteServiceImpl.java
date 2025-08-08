package com.deliverytech.delivery.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteServiceImpl.class);

    private final AuthService authService;
    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper mapper;

    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    @Override
    public RestauranteResponse cadastrarRestaurante(RestauranteRequest request) {
        try {
            Restaurante restaurante = mapper.toEntity(request);
            restaurante.setAtivo(true); // Ativa por padrão
            Restaurante salvo = restauranteRepository.save(restaurante);
            logger.info("[{}] Restaurante cadastrado com sucesso: id={}", getCorrelationId(), salvo.getId());
            return mapper.toResponse(salvo);
        } catch (Exception e) {
            logger.error("[{}] Erro ao cadastrar restaurante", getCorrelationId(), e);
            throw new RuntimeException("Erro ao cadastrar restaurante", e);
        }
    }

    @Override
    public RestauranteResponse buscarRestaurantePorId(Long id) {
        try {
            Restaurante restaurante = buscarOuLancar(id);
            return mapper.toResponse(restaurante);
        } catch (EntityNotFoundException e) {
            logger.warn("[{}] Restaurante não encontrado para id={}", getCorrelationId(), id);
            throw e;
        } catch (Exception e) {
            logger.error("[{}] Erro ao buscar restaurante por id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao buscar restaurante", e);
        }
    }

    @Override
    public RestauranteResponse atualizarRestaurante(Long id, RestauranteRequest request) {
        authService.getUsuarioAutenticado();

        if (!SecurityUtils.hasRole("ADMIN") && !isOwner(id)) {
            String msg = "Você não tem permissão para atualizar este restaurante id=" + id;
            logger.warn("[{}] {}", getCorrelationId(), msg);
            throw new ValidationException(msg);
        }

        try {
            Restaurante restaurante = buscarOuLancar(id);
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
            logger.error("[{}] Erro ao atualizar restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao atualizar restaurante", e);
        }
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesPorCategoria(String categoria) {
        try {
            List<Restaurante> restaurantes = restauranteRepository.findByCategoriaIgnoreCase(categoria);
            return restaurantes.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            logger.error("[{}] Erro ao buscar restaurantes por categoria={}", getCorrelationId(), categoria, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesDisponiveis() {
        try {
            List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();
            return restaurantes.stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (Exception e) {
            logger.error("[{}] Erro ao buscar restaurantes disponíveis", getCorrelationId(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesComFiltros(String categoria, Boolean ativo) {
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
            logger.error("[{}] Erro ao buscar restaurantes com filtros: categoria={}, ativo={}", getCorrelationId(), categoria, ativo, e);
            return Collections.emptyList();
        }
    }

    @Override
    public RestauranteResponse alterarStatusRestaurante(Long id) {
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
            logger.error("[{}] Erro ao alterar status do restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao alterar status do restaurante", e);
        }
    }

    @Override
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
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
            logger.error("[{}] Erro ao calcular taxa de entrega para restaurante id={}", getCorrelationId(), restauranteId, e);
            throw new RuntimeException("Erro ao calcular taxa de entrega", e);
        }
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesProximos(String cep) {
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
            logger.error("[{}] Erro ao buscar restaurantes próximos para cep={}", getCorrelationId(), cep, e);
            return Collections.emptyList();
        }
    }

    private Restaurante buscarOuLancar(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
    }

    @Override
    public boolean isOwner(Long restauranteId) {
        Restaurante restaurante = buscarOuLancar(restauranteId);
        Usuario logado = authService.getUsuarioAutenticado();
        return restaurante.getUsuario() != null && restaurante.getUsuario().getId().equals(logado.getId());
    }

    @Override
    public void removerRestaurante(Long id) {
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
            logger.error("[{}] Erro ao remover restaurante id={}", getCorrelationId(), id, e);
            throw new RuntimeException("Erro ao remover restaurante", e);
        }
    }
}
