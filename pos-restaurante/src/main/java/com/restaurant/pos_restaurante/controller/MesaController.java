package com.restaurant.pos_restaurante.controller;

import com.restaurant.pos_restaurante.dto.MesaDTO;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import com.restaurant.pos_restaurante.dto.MesaRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<MesaDTO>> getMesas(
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(
            mesaService.getMesas(
                usuario.getRestaurante().getId()
            )
        );
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MesaDTO>> getMesasDisponibles(
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(
            mesaService.getDisponibles(
                usuario.getRestaurante().getId()
            )
        );
    }

    @PostMapping
    public ResponseEntity<MesaDTO> crearMesa(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid MesaRequest request) {

        return ResponseEntity.ok(
            mesaService.crearMesa(
                usuario.getRestaurante().getId(),
                request
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> actualizarMesa(
            @PathVariable UUID id,
            @RequestBody MesaRequest request) {

        return ResponseEntity.ok(
            mesaService.actualizarMesa(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMesa(
            @PathVariable UUID id) {

        mesaService.eliminarMesa(id);

        return ResponseEntity.noContent().build();
    }
}