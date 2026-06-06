package com.restaurant.pos_restaurante.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.pos_restaurante.dto.LoginRequest;
import com.restaurant.pos_restaurante.dto.LoginResponse;
import com.restaurant.pos_restaurante.dto.RegisterRequest;
import com.restaurant.pos_restaurante.dto.RestauranteDTO;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/perfil")
public ResponseEntity<?> getPerfil(
        @AuthenticationPrincipal Usuario usuario) {

    System.out.println("USUARIO:");
    System.out.println(usuario);

    if (usuario == null) {
        return ResponseEntity.badRequest().body("Usuario null");
    }

    return ResponseEntity.ok(
        authService.getPerfil(usuario.getEmail()));
}
}
