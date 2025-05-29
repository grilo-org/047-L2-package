package com.product.dto;

import com.product.model.CaixaUsada;
import lombok.Data;

import java.util.List;

@Data
public class PedidoResponse {
    private int pedido_id;
    private List<CaixaUsada> caixas;
}
