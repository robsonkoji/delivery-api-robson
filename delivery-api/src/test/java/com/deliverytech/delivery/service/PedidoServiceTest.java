package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.dto.response.ClienteResponse;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.enums.Role;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.exception.BusinessException;
import com.deliverytech.delivery.mapper.PedidoMapper;
import com.deliverytech.delivery.metrics.PedidoMetrics;
import com.deliverytech.delivery.repository.*;
import com.deliverytech.delivery.service.impl.PedidoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private ClienteRepository clienteRepository;
    @Mock private RestauranteRepository restauranteRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private PedidoMapper pedidoMapper;
    @Mock private PedidoMetrics pedidoMetrics; // Adicionado para evitar NPE

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Usuario usuarioCliente;
    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        // Usuário cliente
        usuarioCliente = new Usuario();
        usuarioCliente.setId(1L);
        usuarioCliente.setNome("Robson");
        usuarioCliente.setRole(Role.CLIENTE);

        // Cliente associado
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setUsuario(usuarioCliente);
        cliente.setAtivo(true);

        // Restaurante ativo
        restaurante = new Restaurante();
        restaurante.setId(10L);
        restaurante.setAtivo(true);
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));
        restaurante.setCategoria("Lanches");
        restaurante.setNome("Restaurante Teste");
        restaurante.setTelefone("1111-2222");
        restaurante.setEndereco("Rua Teste, 456");
        Usuario usuarioRestaurante = new Usuario();
        usuarioRestaurante.setId(2L);
        usuarioRestaurante.setRole(Role.RESTAURANTE);
        restaurante.setUsuario(usuarioRestaurante);

        // Produto disponível e pertencente ao restaurante
        produto = new Produto();
        produto.setId(100L);
        produto.setNome("X-Salada");
        produto.setPreco(new BigDecimal("20.00"));
        produto.setEstoque(10);
        produto.setDisponivel(true);
        produto.setCategoria("Lanche");
        produto.setDescricao("X-Salada clássico com queijo e alface");
        produto.setRestaurante(restaurante);

        // Stubs comuns
        when(usuarioService.getUsuarioLogado()).thenReturn(usuarioCliente);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        // Simula o timer para não quebrar o teste
        when(pedidoMetrics.startTimer()).thenReturn(null);
        doNothing().when(pedidoMetrics).stopTimer(any(), any(), any());
        doNothing().when(pedidoMetrics).incrementarPedidosPorStatus(any());

        // Pedido salvo retorna com id e status
        when(pedidoRepository.save(any())).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0, Pedido.class);
            p.setId(500L);
            p.setStatus(StatusPedido.PENDENTE);
            return p;
        });

        // Mapper padrão para sucesso
        when(pedidoMapper.toResponse(any())).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0, Pedido.class);
            PedidoResponse resp = new PedidoResponse();
            resp.setId(pedido.getId());
            resp.setStatus(pedido.getStatus());
            ClienteResponse cr = new ClienteResponse();
            cr.setId(cliente.getId());
            resp.setCliente(cr);
            RestauranteResponse rr = new RestauranteResponse();
            rr.setId(restaurante.getId());
            resp.setRestaurante(rr);
            BigDecimal subtotal = produto.getPreco().multiply(new BigDecimal(2));
            resp.setTaxaEntrega(restaurante.getTaxaEntrega());
            resp.setTotal(subtotal.add(restaurante.getTaxaEntrega()));
            return resp;
        });
    }

    @Test
    void deveCriarPedidoComSucesso() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(restaurante.getId());
        request.setEnderecoEntrega("Rua Teste, 123");
        request.setItens(List.of(new ItemPedidoRequest(produto.getId(), 2)));

        PedidoResponse response = pedidoService.criarPedido(request);

        assertNotNull(response, "Resposta do pedido não pode ser null");
        assertEquals(StatusPedido.PENDENTE, response.getStatus());
        assertEquals(cliente.getId(), response.getCliente().getId());
        assertNotNull(response.getRestaurante(), "Restaurante no response deveria estar preenchido");
        assertEquals(restaurante.getId(), response.getRestaurante().getId());

        BigDecimal esperadoItens = produto.getPreco().multiply(new BigDecimal(2));
        BigDecimal esperadoTotal = esperadoItens.add(restaurante.getTaxaEntrega());
        assertEquals(0, response.getTotal().compareTo(esperadoTotal));

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoMapper).toResponse(captor.capture());
        assertEquals(StatusPedido.PENDENTE, captor.getValue().getStatus());
    }

    @Test
    void deveLancarErroQuandoRestauranteInativo() {
        Restaurante restauranteInativo = new Restaurante();
        restauranteInativo.setId(11L);
        restauranteInativo.setAtivo(false);
        restauranteInativo.setTaxaEntrega(new BigDecimal("5.00"));
        restauranteInativo.setCategoria("Lanches");
        restauranteInativo.setNome("Inativo");
        restauranteInativo.setTelefone("0000");
        restauranteInativo.setEndereco("Rua Inativa, 123");
        Usuario ur = new Usuario();
        ur.setId(3L);
        ur.setRole(Role.RESTAURANTE);
        restauranteInativo.setUsuario(ur);

        when(restauranteRepository.findById(restauranteInativo.getId())).thenReturn(Optional.of(restauranteInativo));

        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(restauranteInativo.getId());
        request.setEnderecoEntrega("Rua Fechada");
        request.setItens(List.of(new ItemPedidoRequest(produto.getId(), 1)));

        BusinessException ex = assertThrows(BusinessException.class, () -> pedidoService.criarPedido(request));
        assertEquals("Restaurante está inativo", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoProdutoIndisponivel() {
        produto.setDisponivel(false);
        when(produtoRepository.findById(produto.getId())).thenReturn(Optional.of(produto));

        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(restaurante.getId());
        request.setEnderecoEntrega("Rua Produto Off");
        request.setItens(List.of(new ItemPedidoRequest(produto.getId(), 1)));

        BusinessException ex = assertThrows(BusinessException.class, () -> pedidoService.criarPedido(request));
        assertEquals("Produto indisponível: X-Salada", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoProdutoNaoPertenceAoRestaurante() {
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(12L);
        outroRestaurante.setAtivo(true);
        outroRestaurante.setTaxaEntrega(new BigDecimal("3.00"));
        outroRestaurante.setCategoria("Outros");
        outroRestaurante.setNome("Outro");
        outroRestaurante.setTelefone("9999");
        outroRestaurante.setEndereco("Rua Diferente, 999");
        Usuario ur = new Usuario();
        ur.setId(4L);
        ur.setRole(Role.RESTAURANTE);
        outroRestaurante.setUsuario(ur);

        when(restauranteRepository.findById(outroRestaurante.getId())).thenReturn(Optional.of(outroRestaurante));

        PedidoRequest request = new PedidoRequest();
        request.setClienteId(cliente.getId());
        request.setRestauranteId(outroRestaurante.getId());
        request.setEnderecoEntrega("Rua Errada");
        request.setItens(List.of(new ItemPedidoRequest(produto.getId(), 1))); // produto do restaurante original

        BusinessException ex = assertThrows(BusinessException.class, () -> pedidoService.criarPedido(request));
        assertTrue(ex.getMessage().contains("Produto"));
        assertTrue(ex.getMessage().contains("não pertence ao restaurante informado"));
    }
}
