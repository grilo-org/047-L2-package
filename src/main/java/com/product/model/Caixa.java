package com.product.model;

import lombok.Data;

import java.util.List;

@Data
public class Caixa {
    private String caixaId;
    private List<Produto> produtos;
    private String observacao;
}
