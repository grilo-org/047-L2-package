package com.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaixaUsada {
    private String caixa_id;
    private List<String> produtos;
    private String observacao;
}