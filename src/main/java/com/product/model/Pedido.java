package com.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "pedidos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private int pedido_id;
    private List<Produto> produtos;
    private LocalDateTime dateTime = LocalDateTime.now();
}