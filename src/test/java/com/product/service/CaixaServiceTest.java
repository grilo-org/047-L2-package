package com.product.service;

import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponse;
import com.product.dto.PedidoResponseWrapper;
import com.product.model.CaixaUsada;
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
public class CaixaServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private CaixaService caixaService;

    private Produto produto1;
    private Produto produto2;
    private Produto produtoGrande;
    private Produto produtoSemDimensoes;
    private Pedido pedido;
    private Pedido pedidoVazio;
    private Pedido pedidoComProdutoGrande;

    @BeforeEach
    void setUp() {
        produto1 = new Produto();
        produto1.setProduto_id("PS5");
        produto1.setDimensoes(new Dimensoes(40, 10, 25));

        produto2 = new Produto();
        produto2.setProduto_id("Volante");
        produto2.setDimensoes(new Dimensoes(40, 30, 30));

        produtoGrande = new Produto();
        produtoGrande.setProduto_id("Cadeira Gamer");
        produtoGrande.setDimensoes(new Dimensoes(120, 60, 70));

        produtoSemDimensoes = new Produto();
        produtoSemDimensoes.setProduto_id("Produto Inválido");

        pedido = new Pedido();
        pedido.setPedido_id(1);
        pedido.setProdutos(Arrays.asList(produto1, produto2));

        pedidoVazio = new Pedido();
        pedidoVazio.setPedido_id(2);
        pedidoVazio.setProdutos(Collections.emptyList());

        pedidoComProdutoGrande = new Pedido();
        pedidoComProdutoGrande.setPedido_id(3);
        pedidoComProdutoGrande.setProdutos(Collections.singletonList(produtoGrande));
    }

    @Test
    void processarPedidos_DeveRetornarResponseWrapper() {
        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Collections.singletonList(pedido));

        PedidoResponseWrapper result = caixaService.processarPedidos(request);

        assertNotNull(result);
        assertEquals(1, result.getPedidos().size());
        verify(pedidoRepository).saveAll(request.getPedidos());
    }

    @Test
    void processarPedidos_ComMultiplosPedidos_DeveProcessarTodos() {
        PedidoRequest request = new PedidoRequest();
        request.setPedidos(Arrays.asList(pedido, pedidoVazio, pedidoComProdutoGrande));

        PedidoResponseWrapper result = caixaService.processarPedidos(request);

        assertNotNull(result);
        assertEquals(3, result.getPedidos().size());
        verify(pedidoRepository).saveAll(request.getPedidos());
    }

    @Test
    void processarPedido_ProdutoGrande_DeveRetornarObservacao() {
        PedidoResponse result = caixaService.processarPedido(pedidoComProdutoGrande);

        assertNotNull(result);
        assertEquals(1, result.getCaixas().size());
        assertNull(result.getCaixas().getFirst().getCaixa_id());
        assertNotNull(result.getCaixas().getFirst().getObservacao());
    }

    @Test
    void processarPedido_SemProdutos_DeveRetornarListaVazia() {
        PedidoResponse result = caixaService.processarPedido(pedidoVazio);

        assertNotNull(result);
        assertTrue(result.getCaixas().isEmpty());
    }

    @Test
    void processarPedido_ProdutoSemDimensoes_DeveLancarExcecao() {
        Pedido pedidoInvalido = new Pedido();
        pedidoInvalido.setPedido_id(4);
        pedidoInvalido.setProdutos(Collections.singletonList(produtoSemDimensoes));

        assertThrows(IllegalArgumentException.class, () -> {
            caixaService.processarPedido(pedidoInvalido);
        });
    }

    @Test
    void encontrarMelhorCaixa_DoisProdutos_DeveRetornarCaixa2() {
        List<Produto> produtos = Arrays.asList(produto1, produto2);

        CaixaUsada result = caixaService.encontrarMelhorCaixa(produtos);

        assertNotNull(result);
        assertEquals("Caixa 2", result.getCaixa_id());
        assertEquals(2, result.getProdutos().size());
    }

    @Test
    void encontrarMelhorCaixa_ProdutoGrande_DeveRetornarNull() {
        List<Produto> produtos = Collections.singletonList(produtoGrande);

        CaixaUsada result = caixaService.encontrarMelhorCaixa(produtos);

        assertNotNull(result);
        assertNull(result.getCaixa_id());
        assertEquals("Produto não cabe em nenhuma caixa disponível.", result.getObservacao());
        assertEquals(1, result.getProdutos().size());
        assertEquals("Cadeira Gamer", result.getProdutos().getFirst());
    }

    @Test
    void encontrarMelhorCaixa_SemProdutos_DeveRetornarNull() {
        List<Produto> produtos = Collections.emptyList();


        assertThrows(IllegalArgumentException.class, () -> {
            caixaService.encontrarMelhorCaixa(produtos);
        });
    }

    @Test
    void encontrarMelhorCaixa_ProdutoPequeno_DeveUsarCaixa1() {
        Produto produtoPequeno = new Produto();
        produtoPequeno.setProduto_id("Jogo");
        produtoPequeno.setDimensoes(new Dimensoes(20, 5, 10));

        CaixaUsada result = caixaService.encontrarMelhorCaixa(Collections.singletonList(produtoPequeno));

        assertNotNull(result);
        assertEquals("Caixa 1", result.getCaixa_id());
    }

    @Test
    void processarPedido_ProdutosQuePrecisamDeMultiplasCaixas_DeveRetornarVariasCaixas() {
        Pedido pedidoMultiplasCaixas = new Pedido();
        pedidoMultiplasCaixas.setPedido_id(5);
        pedidoMultiplasCaixas.setProdutos(Arrays.asList(
                produto1,
                produto2,
                new Produto("TV", new Dimensoes(100, 10, 50)),
                new Produto("Soundbar", new Dimensoes(80, 10, 15))
        ));

        PedidoResponse result = caixaService.processarPedido(pedidoMultiplasCaixas);

        assertNotNull(result);
        assertTrue(result.getCaixas().size() > 1);
    }
}