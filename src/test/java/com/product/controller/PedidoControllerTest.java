package com.product.controller;

import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponse;
import com.product.dto.PedidoResponseWrapper;
import com.product.model.CaixaUsada;
import com.product.model.Dimensoes;
import com.product.model.Pedido;
import com.product.model.Produto;
import com.product.service.CaixaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private CaixaService caixaService;

    @InjectMocks
    private PedidoController pedidoController;

    @Test
    void processarPedidosTestOk() {
        Produto produto = new Produto();
        produto.setProduto_id("PS5");
        produto.setDimensoes(new Dimensoes(40, 10, 25));

        Pedido pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(Collections.singletonList(produto));

        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Collections.singletonList(pedido));

        CaixaUsada caixaUsada = new CaixaUsada();
        caixaUsada.setCaixa_id("Caixa 2");
        caixaUsada.setProdutos(List.of("PS5"));

        PedidoResponse response = new PedidoResponse();
        response.setPedido_id(1);
        response.setCaixas(List.of(caixaUsada));

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        wrapper.setPedidos(List.of(response));

        when(caixaService.processarPedidos(request)).thenReturn(wrapper);

        PedidoResponseWrapper resultado = pedidoController.processarPedidos(request);

        assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getPedidos().size());
        Assertions.assertEquals(1, resultado.getPedidos().getFirst().getPedido_id());
        Assertions.assertEquals("Caixa 2", resultado.getPedidos().getFirst().getCaixas().getFirst().getCaixa_id());
        verify(caixaService, times(1)).processarPedidos(request);
    }

    @Test
    void processarPedidosTest_DeveLidarComPedidoVazio() {
        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Collections.emptyList());

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        wrapper.setPedidos(Collections.emptyList());

        when(caixaService.processarPedidos(request)).thenReturn(wrapper);

        PedidoResponseWrapper resultado = pedidoController.processarPedidos(request);

        assertNotNull(resultado);
        Assertions.assertTrue(resultado.getPedidos().isEmpty());
        verify(caixaService, times(1)).processarPedidos(request);
    }

    @Test
    void processarPedidosTest_DeveLidarComProdutoGrande() {
        Produto produtoGrande = new Produto();
        produtoGrande.setProduto_id("Cadeira Gamer");
        produtoGrande.setDimensoes(new Dimensoes(120, 60, 70));

        Pedido pedido = new Pedido();
        pedido.setPedido_id(5);
        pedido.setProdutos(List.of(produtoGrande));

        PedidoRequest request = new PedidoRequest();
        request.setPedidos(List.of(pedido));

        CaixaUsada caixaUsada = new CaixaUsada();
        caixaUsada.setCaixa_id(null);
        caixaUsada.setProdutos(List.of("Cadeira Gamer"));
        caixaUsada.setObservacao("Produto não cabe em nenhuma caixa disponível.");

        PedidoResponse response = new PedidoResponse();
        response.setPedido_id(5);
        response.setCaixas(List.of(caixaUsada));

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        wrapper.setPedidos(List.of(response));

        when(caixaService.processarPedidos(request)).thenReturn(wrapper);

        PedidoResponseWrapper resultado = pedidoController.processarPedidos(request);

        assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getPedidos().size());
        Assertions.assertNull(resultado.getPedidos().getFirst().getCaixas().getFirst().getCaixa_id());
        assertNotNull(resultado.getPedidos().getFirst().getCaixas().getFirst().getObservacao());
        verify(caixaService, times(1)).processarPedidos(request);
    }
}