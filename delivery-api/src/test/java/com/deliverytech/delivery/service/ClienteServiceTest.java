package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.service.impl.ClienteServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteServiceImpl clienteService;  // Sem @InjectMocks!

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Instancia manual usando construtor sem MeterRegistry
        clienteService = new ClienteServiceImpl(clienteRepository);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Robson");
        cliente.setEmail("robson@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A");
        cliente.setAtivo(true);
    }

    @Test
    void testeCadastrarCliente_Sucesso() {
        ClienteRequest request = new ClienteRequest("Robson", "robson@email.com", "11999999999", "Rua A");

        when(clienteRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.cadastrarCliente(request);

        assertNotNull(resultado);
        assertEquals("Robson", resultado.getNome());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void cadastrarCliente_DeveLancarExcecao_QuandoEmailDuplicado() {
        ClienteRequest request = new ClienteRequest("Robson", "robson@email.com", "11999999999", "Rua A");

        when(clienteRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> clienteService.cadastrarCliente(request));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void buscarPorId_Existente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente encontrado = clienteService.buscarClientePorId(1L);

        assertNotNull(encontrado);
        assertEquals(cliente.getNome(), encontrado.getNome());
    }

    @Test
    void buscarPorId_Inexistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorId(1L));
    }
}
