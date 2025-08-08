package com.deliverytech.delivery.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.service.ClienteService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;

@Service
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;
    private final MeterRegistry meterRegistry;

    @Autowired
    // Construtor para produção, com métricas
    public ClienteServiceImpl(ClienteRepository clienteRepository, MeterRegistry meterRegistry) {
        this.clienteRepository = clienteRepository;
        this.meterRegistry = meterRegistry;

        if (this.meterRegistry != null) {
            Gauge.builder("clientes.ativos.total", this, ClienteServiceImpl::contarClientesAtivos)
                 .description("Número de clientes ativos")
                 .register(meterRegistry);
        }
    }

    // Construtor alternativo para testes, sem métricas
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        this.meterRegistry = null;  // Não usa métricas nos testes
    }

    private String getCorrelationId() {
        String correlationId = MDC.get("correlationId");
        return correlationId != null ? correlationId : "N/A";
    }

    private RuntimeException logAndWrap(String msg, Exception ex) {
        logger.error("[{}] {}", getCorrelationId(), msg, ex);
        return new RuntimeException(msg, ex);
    }

    @Override
    public Cliente cadastrarCliente(ClienteRequest request) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            if (clienteRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Cliente com email já cadastrado");
            }

            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());
            cliente.setEndereco(request.getEndereco());
            cliente.setAtivo(true);

            Cliente salvo = clienteRepository.save(cliente);

            if (meterRegistry != null) {
                meterRegistry.counter("clientes.cadastrados.total").increment();
            }

            logger.info("[{}] Cliente cadastrado com sucesso: id={}, email={}", getCorrelationId(), salvo.getId(), salvo.getEmail());

            return salvo;
        } catch (BusinessException bex) {
            logger.warn("[{}] BusinessException ao cadastrar cliente: {}", getCorrelationId(), bex.getMessage());
            throw bex;
        } catch (Exception ex) {
            throw logAndWrap("Erro ao cadastrar cliente com email " + request.getEmail(), ex);
        } finally {
            if (sample != null) {
                sample.stop(meterRegistry.timer("clientes.cadastro.tempo"));
            }
        }
    }

    private double contarClientesAtivos() {
        return clienteRepository.countByAtivoTrue();
    }

    @Override
    public Cliente buscarClientePorId(Long id) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
            logger.debug("[{}] Cliente encontrado por ID: {}", getCorrelationId(), id);
            return cliente;
        } catch (EntityNotFoundException ex) {
            logger.warn("[{}] Cliente não encontrado por ID: {}", getCorrelationId(), id);
            throw ex;
        } catch (Exception ex) {
            throw logAndWrap("Erro inesperado ao buscar cliente por ID " + id, ex);
        }
    }

    @Override
    public Cliente buscarClientePorEmail(String email) {
        try {
            Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com email: " + email));
            logger.debug("[{}] Cliente encontrado por email: {}", getCorrelationId(), email);
            return cliente;
        } catch (EntityNotFoundException ex) {
            logger.warn("[{}] Cliente não encontrado por email: {}", getCorrelationId(), email);
            throw ex;
        } catch (Exception ex) {
            throw logAndWrap("Erro inesperado ao buscar cliente por email " + email, ex);
        }
    }

    @Override
    public Cliente atualizarCliente(Long id, ClienteRequest request) {
        try {
            Cliente cliente = buscarClientePorId(id);

            if (clienteRepository.existsByEmail(request.getEmail()) &&
                !cliente.getEmail().equals(request.getEmail())) {
                throw new BusinessException("Cliente com email já cadastrado");
            }

            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());
            cliente.setEndereco(request.getEndereco());

            Cliente atualizado = clienteRepository.save(cliente);
            logger.info("[{}] Cliente atualizado: id={}, email={}", getCorrelationId(), id, atualizado.getEmail());

            return atualizado;
        } catch (BusinessException bex) {
            logger.warn("[{}] BusinessException ao atualizar cliente: {}", getCorrelationId(), bex.getMessage());
            throw bex;
        } catch (Exception ex) {
            throw logAndWrap("Erro ao atualizar cliente id " + id, ex);
        }
    }

    @Override
    public Cliente ativarDesativarCliente(Long id) {
        try {
            Cliente cliente = buscarClientePorId(id);
            if (cliente.isAtivo()) {
                cliente.inativar();
                logger.info("[{}] Cliente inativado: id={}", getCorrelationId(), id);
            } else {
                cliente.reativar();
                logger.info("[{}] Cliente reativado: id={}", getCorrelationId(), id);
            }
            return clienteRepository.save(cliente);
        } catch (Exception ex) {
            throw logAndWrap("Erro ao ativar/desativar cliente id " + id, ex);
        }
    }

    @Override
    public List<Cliente> listarClientesAtivos() {
        try {
            return clienteRepository.findByAtivoTrue();
        } catch (Exception ex) {
            throw logAndWrap("Erro ao listar clientes ativos", ex);
        }
    }

    @Override
    public List<Cliente> buscarClientesPorNome(String nome) {
        try {
            return clienteRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
        } catch (Exception ex) {
            throw logAndWrap("Erro ao buscar clientes por nome " + nome, ex);
        }
    }

    @Override
    public List<Cliente> buscarClientesPorTelefone(String telefone) {
        try {
            return clienteRepository.findByTelefoneContainingAndAtivoTrue(telefone);
        } catch (Exception ex) {
            throw logAndWrap("Erro ao buscar clientes por telefone " + telefone, ex);
        }
    }

    @Override
    public List<Cliente> buscarClientesPorEndereco(String endereco) {
        try {
            return clienteRepository.findByEnderecoContainingIgnoreCaseAndAtivoTrue(endereco);
        } catch (Exception ex) {
            throw logAndWrap("Erro ao buscar clientes por endereco " + endereco, ex);
        }
    }

    @Override
    public Cliente inativarCliente(Long id) {
        try {
            Cliente cliente = buscarClientePorId(id);
            cliente.inativar();
            Cliente salvo = clienteRepository.save(cliente);
            logger.info("[{}] Cliente inativado: id={}", getCorrelationId(), id);
            return salvo;
        } catch (Exception ex) {
            throw logAndWrap("Erro ao inativar cliente id " + id, ex);
        }
    }

    @Override
    public Cliente ativarCliente(Long id) {
        try {
            Cliente cliente = buscarClientePorId(id);
            cliente.reativar();
            Cliente salvo = clienteRepository.save(cliente);
            logger.info("[{}] Cliente reativado: id={}", getCorrelationId(), id);
            return salvo;
        } catch (Exception ex) {
            throw logAndWrap("Erro ao ativar cliente id " + id, ex);
        }
    }

    @Override
    public List<Cliente> listarTodosClientes() {
        try {
            return clienteRepository.findAll();
        } catch (Exception ex) {
            throw logAndWrap("Erro ao listar todos clientes", ex);
        }
    }
}
