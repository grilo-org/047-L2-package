package com.product.dto;

import com.product.model.Pedido;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {
    private List<Pedido> pedidos;
}