package com.restaurant.pos_restaurante.controller;

import com.restaurant.pos_restaurante.service.IAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/predicciones")
public class PrediccionController {

    private final IAService iaService;

    public PrediccionController(IAService iaService) {
        this.iaService = iaService;
    }

    /**
     * Predice ventas de un producto
     * GET /api/predicciones/ventas/{productoId}?dias=30
     */
    @GetMapping("/ventas/{productoId}")
    public ResponseEntity<Map<String, Object>> predecirVentas(
            @PathVariable String productoId,
            @RequestParam(defaultValue = "30") int dias) {

        Map<String, Object> resultado = iaService.predecirVentas(productoId, dias);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Predice días hasta agotamiento de un insumo
     * GET /api/predicciones/inventario/{insumoId}
     */
    @GetMapping("/inventario/{insumoId}")
    public ResponseEntity<Map<String, Object>> predecirInventario(
            @PathVariable String insumoId) {

        Map<String, Object> resultado = iaService.predecirInventario(insumoId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista productos disponibles para predicción
     * GET /api/predicciones/productos/{restauranteId}
     */
    @GetMapping("/productos/{restauranteId}")
    public ResponseEntity<Map<String, Object>> listarProductos(
            @PathVariable String restauranteId) {

        Map<String, Object> resultado = iaService.listarProductosIA(restauranteId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista insumos con su stock actual
     * GET /api/predicciones/insumos/{restauranteId}
     */
    @GetMapping("/insumos/{restauranteId}")
    public ResponseEntity<Map<String, Object>> listarInsumos(
            @PathVariable String restauranteId) {

        Map<String, Object> resultado = iaService.listarInsumosIA(restauranteId);
        return ResponseEntity.ok(resultado);
    }
}