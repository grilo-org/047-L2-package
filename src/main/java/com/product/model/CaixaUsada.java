package com.product.model;

import lombok.Data;

import java.util.List;

@Data
public class CaixaUsada {
    private String caixa_id;
    private List<String> produtos;
    private String observacao;
}