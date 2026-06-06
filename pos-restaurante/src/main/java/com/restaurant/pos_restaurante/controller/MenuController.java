package com.restaurant.pos_restaurante.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;

import com.restaurant.pos_restaurante.dto.CategoriaDTO;
import com.restaurant.pos_restaurante.dto.ProductoDTO;
import com.restaurant.pos_restaurante.dto.ProductoRequest;
import com.restaurant.pos_restaurante.dto.RecetaIngredienteDTO;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDTO>> getCategorias(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(menuService.getCategorias(usuario.getRestaurante().getId()));
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaDTO> crearCategoria(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(menuService.crearCategoria(
            usuario.getRestaurante().getId(), body.get("nombre")));
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> getProductos(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) UUID categoriaId) {
        return ResponseEntity.ok(menuService.getProductos(
            usuario.getRestaurante().getId(), categoriaId));
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoDTO> crearProducto(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(menuService.crearProducto(
            usuario.getRestaurante().getId(), request));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(menuService.actualizarProducto(id, request));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable UUID id) {
        menuService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/productos/{id}/receta")
    public ResponseEntity<List<RecetaIngredienteDTO>> getReceta(@PathVariable UUID id) {
        return ResponseEntity.ok(menuService.getReceta(id));
    }

    @PostMapping("/productos/{id}/receta")
    public ResponseEntity<RecetaIngredienteDTO> agregarIngrediente(
            @PathVariable UUID id,
            @Valid @RequestBody RecetaIngredienteDTO dto) {
        return ResponseEntity.ok(menuService.agregarIngrediente(id, dto));
    }
    @PatchMapping("/productos/{id}/disponibilidad")
public ResponseEntity<Void> cambiarDisponibilidad(
        @PathVariable UUID id,
        @RequestParam boolean disponible) {

    menuService.cambiarDisponibilidad(id, disponible);

    return ResponseEntity.ok().build();
}

    @DeleteMapping("/receta/{ingredienteId}")
    public ResponseEntity<Void> eliminarIngrediente(@PathVariable UUID ingredienteId) {
        menuService.eliminarIngrediente(ingredienteId);
        return ResponseEntity.noContent().build();
    }
}