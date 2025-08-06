package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.response.ItemPedidoResponse;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.entity.ItemPedido;
import com.deliverytech.delivery.entity.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    private final ClienteMapper clienteMapper;
    private final RestauranteMapper restauranteMapper;

    public PedidoMapper() {
        this.clienteMapper = new ClienteMapper();
        this.restauranteMapper = new RestauranteMapper();
    }

    public PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens().stream()
                .map(this::toItemResponse)
                .toList();

        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setCliente(clienteMapper.toResponse(pedido.getCliente())); // cliente ok
        response.setRestaurante(restauranteMapper.toResponse(pedido.getRestaurante())); // nova linha: popula o objeto
        response.setRestauranteId(pedido.getRestaurante().getId()); // mantém por compatibilidade se precisar
        response.setEnderecoEntrega(pedido.getEnderecoEntrega());
        response.setObservacoes(pedido.getObservacoes());
        response.setStatus(pedido.getStatus());
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
