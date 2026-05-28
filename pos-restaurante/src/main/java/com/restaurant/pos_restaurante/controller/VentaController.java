package com.restaurant.pos_restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.pos_restaurante.dto.CerrarPedidoRequest;
import com.restaurant.pos_restaurante.dto.VentaDTO;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaDTO>> getVentas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ventaService.getVentas(usuario.getRestaurante().getId()));
    }

    @PostMapping("/cerrar/{pedidoId}")
    public ResponseEntity<VentaDTO> cerrarPedido(
            @PathVariable UUID pedidoId,
            @Valid @RequestBody CerrarPedidoRequest request) {
        return ResponseEntity.ok(ventaService.cerrarPedido(pedidoId, request));
    }
}
