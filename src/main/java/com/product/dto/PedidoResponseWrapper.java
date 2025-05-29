package com.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoResponseWrapper {
    private List<PedidoResponse> pedidos;
}