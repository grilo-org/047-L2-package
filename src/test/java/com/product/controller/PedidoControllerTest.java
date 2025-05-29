package com.product.controller;

import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponse;
import com.product.dto.PedidoResponseWrapper;
import com.product.model.Pedido;
import com.product.model.Produto;
import com.product.service.CaixaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private CaixaService caixaService;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void processarPedidosTestOk() {
        Pedido pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(Collections.singletonList(new Produto()));

        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Collections.singletonList(pedido));

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        when(caixaService.processarPedidos(request)).thenReturn(wrapper);

        PedidoResponseWrapper resultado = pedidoController.processarPedidos(request);

        assertNotNull(resultado);
    }
}