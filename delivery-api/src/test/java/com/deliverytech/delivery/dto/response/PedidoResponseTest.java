package com.deliverytech.delivery.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery.enums.StatusPedido;

import org.junit.jupiter.api.Test;

class PedidoResponseTest {

    @Test
    void deveSetarEObterCamposCorretamente() {
        PedidoResponse pedido = new PedidoResponse();

        Long id = 1001L;
        ClienteResponse cliente = new ClienteResponse();
        RestauranteResponse restaurante = new RestauranteResponse();
        Long restauranteId = 10L;
        String enderecoEntrega = "Av. Paulista, 1000 - CEP 01310-100";
        String observacoes = "Sem cebola, por favor";
        StatusPedido status = StatusPedido.PENDENTE;
        LocalDateTime dataPedido = LocalDateTime.of(2025, 8, 2, 13, 0);
        BigDecimal taxaEntrega = new BigDecimal("7.50");
        BigDecimal total = new BigDecimal("57.40");
        List<ItemPedidoResponse> itens = List.of(new ItemPedidoResponse());

        pedido.setId(id);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setRestauranteId(restauranteId);
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setObservacoes(observacoes);
        pedido.setStatus(status);
        pedido.setDataPedido(dataPedido);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setTotal(total);
        pedido.setItens(itens);

        assertThat(pedido.getId()).isEqualTo(id);
        assertThat(pedido.getCliente()).isEqualTo(cliente);
        assertThat(pedido.getRestaurante()).isEqualTo(restaurante);
        assertThat(pedido.getRestauranteId()).isEqualTo(restauranteId);
        assertThat(pedido.getEnderecoEntrega()).isEqualTo(enderecoEntrega);
        assertThat(pedido.getObservacoes()).isEqualTo(observacoes);
        assertThat(pedido.getStatus()).isEqualTo(status);
        assertThat(pedido.getDataPedido()).isEqualTo(dataPedido);
        assertThat(pedido.getTaxaEntrega()).isEqualByComparingTo(taxaEntrega);
        assertThat(pedido.getTotal()).isEqualByComparingTo(total);
        assertThat(pedido.getItens()).isEqualTo(itens);
    }
}