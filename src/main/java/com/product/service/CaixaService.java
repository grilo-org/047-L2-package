package com.product.service;

import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponse;
import com.product.dto.PedidoResponseWrapper;
import com.product.model.CaixaUsada;
import com.product.model.Dimensoes;
import com.product.model.Pedido;
import com.product.model.Produto;
import com.product.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CaixaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    private static final List<Dimensoes> CAIXAS_DISPONIVEIS = List.of(
            new Dimensoes(30, 40, 80),  // Caixa 1
            new Dimensoes(80, 50, 40),  // Caixa 2
            new Dimensoes(50, 80, 60)   // Caixa 3
    );


    public PedidoResponseWrapper processarPedidos(PedidoRequest pedidoRequest) {
        pedidoRepository.saveAll(pedidoRequest.getPedidos());

        List<PedidoResponse> responses = new ArrayList<>();

        for (Pedido pedido : pedidoRequest.getPedidos()) {
            responses.add(processarPedido(pedido));
        }

        PedidoResponseWrapper wrapper = new PedidoResponseWrapper();
        wrapper.setPedidos(responses);
        return wrapper;
    }

    PedidoResponse processarPedido(Pedido pedido) {
        for (Produto produto : pedido.getProdutos()) {
            Dimensoes dim = produto.getDimensoes();
            if (dim == null) {
                throw new IllegalArgumentException("Dimensões não informadas para o produto: " + produto.getProduto_id());
            }
            if (dim.getAltura() <= 0 || dim.getLargura() <= 0 || dim.getComprimento() <= 0) {
                throw new IllegalArgumentException("Dimensões inválidas para o produto: " + produto.getProduto_id());
            }
        }

        PedidoResponse response = new PedidoResponse();
        response.setPedido_id(pedido.getPedido_id());

        List<CaixaUsada> caixasUsadas = new ArrayList<>();
        List<Produto> produtosRestantes = new ArrayList<>(pedido.getProdutos());

        while (!produtosRestantes.isEmpty()) {
            CaixaUsada melhorCaixa = encontrarMelhorCaixa(produtosRestantes);
            caixasUsadas.add(melhorCaixa);

            produtosRestantes.removeIf(prod -> melhorCaixa.getProdutos().contains(prod.getProduto_id()));
        }

        response.setCaixas(caixasUsadas);
        return response;
    }

    CaixaUsada encontrarMelhorCaixa(List<Produto> produtos) {
        if (produtos.isEmpty())
            throw new IllegalArgumentException("A lista de produtos está vazia");

        CaixaUsada caixaUsada = new CaixaUsada();
        List<String> produtosNaCaixa = new ArrayList<>();

        for (Produto produto : produtos) {
            if (!cabeEmAlgumaCaixa(produto.getDimensoes())) {
                caixaUsada.setCaixa_id(null);
                caixaUsada.setObservacao("Produto não cabe em nenhuma caixa disponível.");
                caixaUsada.setProdutos(List.of(produto.getProduto_id()));
                return caixaUsada;
            }
        }

        for (int i = 0; i < CAIXAS_DISPONIVEIS.size(); i++) {
            Dimensoes caixa = CAIXAS_DISPONIVEIS.get(i);
            List<String> produtosAtual = new ArrayList<>();

            for (Produto produto : produtos) {
                if (cabeNaCaixa(produto.getDimensoes(), caixa)) {
                    produtosAtual.add(produto.getProduto_id());
                }
            }

            if (produtosAtual.size() > produtosNaCaixa.size()) {
                produtosNaCaixa = produtosAtual;
                caixaUsada.setCaixa_id("Caixa " + (i + 1));
                caixaUsada.setProdutos(produtosAtual);
            }
        }

        if (produtosNaCaixa.isEmpty() && !produtos.isEmpty()) {
            caixaUsada.setCaixa_id(null);
            caixaUsada.setObservacao("Produto não cabe em nenhuma caixa disponível.");
            caixaUsada.setProdutos(List.of(produtos.get(0).getProduto_id()));
        }

        return caixaUsada;
    }

    private boolean cabeEmAlgumaCaixa(Dimensoes dimensoesProduto) {
        return CAIXAS_DISPONIVEIS.stream().anyMatch(caixa -> cabeNaCaixa(dimensoesProduto, caixa));
    }

    private boolean cabeNaCaixa(Dimensoes dimensoesProduto, Dimensoes caixa) {
        return dimensoesProduto.getAltura() <= caixa.getAltura() &&
                dimensoesProduto.getLargura() <= caixa.getLargura() &&
                dimensoesProduto.getComprimento() <= caixa.getComprimento();
    }
}