package com.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponse;
import com.product.dto.PedidoResponseWrapper;
import com.product.model.CaixaUsada;
import com.product.model.Dimensoes;
import com.product.model.Pedido;
import com.product.model.Produto;
import com.product.service.CaixaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CaixaService caixaService;

    @Test
    void processarPedidos_DeveRetornarOk() throws Exception {
        PedidoRequest request = new PedidoRequest();
        Pedido pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(List.of(
                new Produto("PS5", new Dimensoes(40, 10, 25)),
                new Produto("Volante", new Dimensoes(40, 30, 30))
        ));
        request.setPedidos(List.of(pedido));

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        PedidoResponse response = new PedidoResponse();
        response.setPedido_id(1);
        response.setCaixas(List.of(new CaixaUsada("Caixa 2", List.of("PS5", "Volante"), null)));
        wrapper.setPedidos(List.of(response));

        when(caixaService.processarPedidos(request)).thenReturn(wrapper);

        mockMvc.perform(post("/api/pedidos/embalar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
