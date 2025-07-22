package com.deliverytech.delivery.config;

import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.model.*;
import com.deliverytech.delivery.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public DataLoader(ClienteRepository clienteRepository,
                      RestauranteRepository restauranteRepository,
                      ProdutoRepository produtoRepository,
                      PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("🔁 Carregando dados de teste...");

        // 3 Clientes
        Cliente cliente1 = new Cliente(null, "João Silva", "joao@email.com", "11999999999", true);
        Cliente cliente2 = new Cliente(null, "Maria Oliveira", "maria@email.com", "11988888888", true);
        Cliente cliente3 = new Cliente(null, "Carlos Souza", "carlos@email.com", "11977777777", true);
        clienteRepository.saveAll(List.of(cliente1, cliente2, cliente3));

        // 2 Restaurantes
        Restaurante r1 = new Restaurante(null, "Pizzaria Napoli", "Pizzaria", "Rua das Flores, 100" , "1133334444", new BigDecimal("4.6"), true);
        Restaurante r2 = new Restaurante(null, "Sushi Yama", "restaurante", "Av. Japão, 200", "1144445555", new BigDecimal("6.9"), true);
        restauranteRepository.saveAll(List.of(r1, r2));

        // 5 Produtos
        Produto p1 = new Produto("Pizza Margherita", "Pizza Margherita", new BigDecimal("40.00"),"pizza", true, r1);
        Produto p2 = new Produto("Pizza Calabresa", "Pizza Calabresa", new BigDecimal("45.00"), "pizza", true, r1);
        Produto p3 = new Produto("Temaki Salmão", "Temaki Salmão", new BigDecimal("30.00"), "comida japonesa", true, r2);
        Produto p4 = new Produto("Sushi Combo", "Sushi Combo", new BigDecimal("55.00"), "comida japonesa", true, r2);
        Produto p5 = new Produto("Refrigerante", "Refrigerante", new BigDecimal("7.00"), "bebidas", true, r1);
        produtoRepository.saveAll(List.of(p1, p2, p3, p4, p5));

        // Pedido 1 (cliente1 com 2 itens do restaurante r1)
        Pedido pedido1 = new Pedido();
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(r1);
        pedido1.setDataCriacao(LocalDateTime.now());
        pedido1.setStatus(StatusPedido.CRIADO);
        pedido1.setItens(List.of(
                new ItemPedido(null, p1, 1, p1.getPreco(), pedido1),
                new ItemPedido(null, p5, 2, p5.getPreco(), pedido1)
        ));
        pedido1.calcularValorTotal();

        // Pedido 2 (cliente2 com 1 item do restaurante r2)
        Pedido pedido2 = new Pedido();
        pedido2.setCliente(cliente2);
        pedido2.setRestaurante(r2);
        pedido2.setDataCriacao(LocalDateTime.now());
        pedido2.setStatus(StatusPedido.CRIADO);
        pedido2.setItens(List.of(
                new ItemPedido(null, p3, 2, p3.getPreco(), pedido2)
        ));
        pedido2.calcularValorTotal();

        pedidoRepository.saveAll(List.of(pedido1, pedido2));

        System.out.println("✅ Dados carregados com sucesso.");
    

        clienteRepository.findByEmail("joao@email.com")
        .ifPresent(c -> System.out.println("Cliente encontrado: " + c.getNome()));

        produtoRepository.findByRestauranteId(r1.getId())
            .forEach(p -> System.out.println("Produto do Restaurante: " + p.getNome()));

        pedidoRepository.findTop10ByOrderByDataPedidoDesc()
            .forEach(p -> System.out.println("Pedido recente: " + p.getId()));

        restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"))
            .forEach(r -> System.out.println("Restaurante com taxa até R$5,00: " + r.getNome()));
        }
}


