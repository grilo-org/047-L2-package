package com.product.service;

import com.product.dto.PedidoRequest;
import com.product.model.Dimensoes;
import com.product.model.Pedido;
import com.product.model.Produto;
import com.product.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaixaServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private CaixaService caixaService;

    private Pedido pedido;
    private Produto produto1, produto2;

    @BeforeEach
    void setUp() {
        produto1 = new Produto();
        produto1.setProduto_id("PS5");
        produto1.setDimensoes(new Dimensoes(40, 10, 25));

        produto2 = new Produto();
        produto2.setProduto_id("Volante");
        produto2.setDimensoes(new Dimensoes(40, 30, 30));

        pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(Arrays.asList(produto1, produto2));
    }

    @Test
    void processarPedidos_DeveRetornarResponseWrapper() {
        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Collections.singletonList(pedido));

        var result = caixaService.processarPedidos(request);

        assertNotNull(result);
        assertEquals(1, result.getPedidos().size());
        verify(pedidoRepository).saveAll(request.getPedidos());
    }

    @Test
    void processarPedido_ProdutoGrande_DeveRetornarObservacao() {
        Produto produtoGrande = new Produto();
        produtoGrande.setProduto_id("Cadeira Gamer");
        produtoGrande.setDimensoes(new Dimensoes(120, 60, 70));

        pedido.setProdutos(Collections.singletonList(produtoGrande));

        var result = caixaService.processarPedido(pedido);

        assertNotNull(result);
        assertEquals(1, result.getCaixas().size());
        assertNull(result.getCaixas().getFirst().getCaixa_id());
        assertNotNull(result.getCaixas().getFirst().getObservacao());
    }

    @Test
    void encontrarMelhorCaixa_DoisProdutos_DeveRetornarCaixa2() {
        List<Produto> produtos = Arrays.asList(produto1, produto2);

        var result = caixaService.encontrarMelhorCaixa(produtos);

        assertNotNull(result);
        assertEquals("Caixa 2", result.getCaixa_id());
        assertEquals(2, result.getProdutos().size());
    }
}