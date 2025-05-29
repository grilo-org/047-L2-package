package com.product.controller;

import com.product.dto.PedidoRequest;
import com.product.dto.PedidoResponseWrapper;
import com.product.service.CaixaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private CaixaService caixaService;

    @PostMapping("/embalar")
    @ResponseStatus(HttpStatus.OK)
    public PedidoResponseWrapper processarPedidos(@RequestBody PedidoRequest pedidoRequest) {
        return caixaService.processarPedidos(pedidoRequest);
    }
}
