/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apiservice.apiservice.controllers;

import com.apiservice.apiservice.models.Client;
import com.apiservice.apiservice.models.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Intel
 */

@RestController
@RequestMapping("/api/bff")
public class BFFController {
    private final WebClient webClient;

    public BFFController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    @GetMapping("/cliente-productos")
    public Mono<Map<String, Object>> getClienteConProductos(@RequestParam String codigoUnico) {
        Mono<Client> cliente = webClient
            .get()
            .uri("http://localhost:8081/api/clients/" + codigoUnico)
            .retrieve()
            .bodyToMono(Client.class);

        Mono<List<Product>> productos = webClient
            .get()
            .uri("http://localhost:8082/api/products/" + codigoUnico)
            .retrieve()
            .bodyToFlux(Product.class)
            .collectList();

        return Mono.zip(cliente, productos)
            .map(tuple -> {
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("cliente", tuple.getT1());
                resultado.put("productosFinancieros", tuple.getT2());
                return resultado;
            });
    }
}