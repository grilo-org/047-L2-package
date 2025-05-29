package com.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.PedidoRequest;
import com.product.model.Dimensoes;
import com.product.model.Pedido;
import com.product.model.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void processarPedidos_Valido_DeveRetornarOkComRespostaCorreta() throws Exception {
        PedidoRequest request = new PedidoRequest();
        Pedido pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(List.of(
                new Produto("PS5", new Dimensoes(40, 10, 25)),
                new Produto("Volante", new Dimensoes(40, 30, 30))
        ));
        request.setPedidos(List.of(pedido));

        mockMvc.perform(post("/api/pedidos/embalar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidos").isArray())
                .andExpect(jsonPath("$.pedidos[0].pedido_id").value(1))
                .andExpect(jsonPath("$.pedidos[0].caixas").isArray())
                .andExpect(jsonPath("$.pedidos[0].caixas[0].caixa_id").value("Caixa 2"))
                .andExpect(jsonPath("$.pedidos[0].caixas[0].produtos").isArray())
                .andExpect(jsonPath("$.pedidos[0].caixas[0].produtos[0]").value("PS5"))
                .andExpect(jsonPath("$.pedidos[0].caixas[0].produtos[1]").value("Volante"));
    }

    @Test
    void processarPedidos_ComProdutoGrande_DeveRetornarObservacao() throws Exception {
        PedidoRequest request = new PedidoRequest();
        Pedido pedido = new Pedido();
        pedido.setPedido_id(2);
        pedido.setProdutos(List.of(
                new Produto("Cadeira Gamer", new Dimensoes(120, 60, 70))
        ));
        request.setPedidos(List.of(pedido));

        mockMvc.perform(post("/api/pedidos/embalar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidos[0].caixas[0].caixa_id").doesNotExist())
                .andExpect(jsonPath("$.pedidos[0].caixas[0].observacao").value("Produto não cabe em nenhuma caixa disponível."))
                .andExpect(jsonPath("$.pedidos[0].caixas[0].produtos[0]").value("Cadeira Gamer"));
    }

    @Test
    void processarPedidos_MultiplosPedidos_DeveProcessarTodos() throws Exception {
        PedidoRequest request = new PedidoRequest();

        Pedido pedido1 = new Pedido();
        pedido1.setPedido_id(1);
        pedido1.setProdutos(List.of(
                new Produto("PS5", new Dimensoes(40, 10, 25))
        ));

        Pedido pedido2 = new Pedido();
        pedido2.setPedido_id(2);
        pedido2.setProdutos(List.of(
                new Produto("Cadeira Gamer", new Dimensoes(120, 60, 70))
        ));

        request.setPedidos(List.of(pedido1, pedido2));

        mockMvc.perform(post("/api/pedidos/embalar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidos.length()").value(2))

                .andExpect(jsonPath("$.pedidos[0].pedido_id").value(1))
                .andExpect(jsonPath("$.pedidos[0].caixas[0].caixa_id").value("Caixa 2"))

                .andExpect(jsonPath("$.pedidos[1].pedido_id").value(2))
                .andExpect(jsonPath("$.pedidos[1].caixas[0].observacao").exists());
    }


}