package com.restaurant.pos_restaurante.controller;

import com.restaurant.pos_restaurante.dto.PedidoDTO;
import com.restaurant.pos_restaurante.dto.PedidoRequest;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.service.PedidoService;
import com.restaurant.pos_restaurante.dto.UpdatePedidoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getPedidos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(pedidoService.getPedidos(usuario.getRestaurante().getId()));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PedidoDTO>> getPedidosActivos(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(pedidoService.getPedidosActivos(usuario.getRestaurante().getId()));
    }

    @PutMapping("/{id}")
public ResponseEntity<PedidoDTO> actualizarPedido(
        @PathVariable UUID id,
        @RequestBody UpdatePedidoRequest request
) {

    return ResponseEntity.ok(
        pedidoService.actualizarPedido(
            id,
            request
        )
    );
}

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(pedidoService.crearPedido(
            usuario.getRestaurante().getId(),
            usuario.getId(),
            request
        ));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        EstadoPedido estado = EstadoPedido.valueOf(body.get("estado"));
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }
}
