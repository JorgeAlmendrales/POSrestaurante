package com.restaurant.pos_restaurante.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.pos_restaurante.dto.InsumoDTO;
import com.restaurant.pos_restaurante.dto.InsumoRequest;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.InventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/insumos")
    public ResponseEntity<List<InsumoDTO>> getInsumos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(inventarioService.getInsumos(usuario.getRestaurante().getId()));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<InsumoDTO>> getAlertas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(inventarioService.getAlertas(usuario.getRestaurante().getId()));
    }

    @PostMapping("/insumos")
    public ResponseEntity<InsumoDTO> crearInsumo(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody InsumoRequest request) {
        return ResponseEntity.ok(inventarioService.crearInsumo(
            usuario.getRestaurante().getId(), request));
    }

    @PutMapping("/insumos/{id}")
    public ResponseEntity<InsumoDTO> actualizarInsumo(
            @PathVariable UUID id,
            @Valid @RequestBody InsumoRequest request) {
        return ResponseEntity.ok(inventarioService.actualizarInsumo(id, request));
    }

    @DeleteMapping("/insumos/{id}")
    public ResponseEntity<Void> eliminarInsumo(
            @PathVariable UUID id) {
        inventarioService.eliminarInsumo(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/insumos/{id}/ajustar")
    public ResponseEntity<InsumoDTO> ajustarStock(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        BigDecimal cantidad = new BigDecimal(body.get("cantidad").toString());
        String motivo = body.getOrDefault("motivo", "Ajuste manual").toString();
        return ResponseEntity.ok(inventarioService.ajustarStock(id, cantidad, motivo));
    }
}