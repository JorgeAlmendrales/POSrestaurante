package com.restaurant.pos_restaurante.controller;

import com.restaurant.pos_restaurante.dto.DashboardDTO;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(dashboardService.getDashboard(usuario.getRestaurante().getId()));
    }
}
