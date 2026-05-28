package com.restaurant.pos_restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.pos_restaurante.dto.ProveedorDTO;
import com.restaurant.pos_restaurante.dto.ProveedorRequest;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.ProveedorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> getProveedores(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(proveedorService.getProveedores(usuario.getRestaurante().getId()));
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crearProveedor(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.crearProveedor(
            usuario.getRestaurante().getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizarProveedor(
            @PathVariable UUID id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable UUID id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
