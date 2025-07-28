package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.response.ItemPedidoResponse;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.entity.ItemPedido;
import com.deliverytech.delivery.entity.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens().stream().map(this::toItemResponse).toList();

        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setClienteId(pedido.getCliente().getId());
        response.setRestauranteId(pedido.getRestaurante().getId());
        response.setEnderecoEntrega(pedido.getEnderecoEntrega());
        response.setObservacoes(pedido.getObservacoes());
        response.setStatus(pedido.getStatus().name());
        response.setDataPedido(pedido.getDataPedido());
        response.setTaxaEntrega(pedido.getTaxaEntrega());
        response.setTotal(pedido.getTotal());
        response.setItens(itens);

        return response;
    }

    private ItemPedidoResponse toItemResponse(ItemPedido item) {
        ItemPedidoResponse response = new ItemPedidoResponse();
        response.setId(item.getId());
        response.setProdutoId(item.getProduto().getId());
        response.setNomeProduto(item.getProduto().getNome());
        response.setQuantidade(item.getQuantidade());
        response.setPrecoUnitario(item.getPrecoUnitario());
        response.setSubtotal(item.getSubtotal());
        return response;
    }
}
