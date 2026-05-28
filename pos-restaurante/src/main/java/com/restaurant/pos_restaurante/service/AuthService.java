package com.restaurant.pos_restaurante.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.pos_restaurante.dto.LoginRequest;
import com.restaurant.pos_restaurante.dto.LoginResponse;
import com.restaurant.pos_restaurante.dto.RegisterRequest;
import com.restaurant.pos_restaurante.dto.RestauranteDTO;
import com.restaurant.pos_restaurante.entity.Restaurante;
import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.enums.Rol;
import com.restaurant.pos_restaurante.repository.RestauranteRepository;
import com.restaurant.pos_restaurante.repository.UsuarioRepository;
import com.restaurant.pos_restaurante.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (restauranteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un restaurante con ese email");
        }

        Restaurante restaurante = new Restaurante();
        restaurante.setNombre(request.getNombre());
        restaurante.setEmail(request.getEmail());
        restaurante.setTelefono(request.getTelefono());
        restaurante.setDireccion(request.getDireccion());
        restaurante.setDescripcion(request.getDescripcion());
        restaurante = restauranteRepository.save(restaurante);

        Usuario admin = new Usuario();
        admin.setRestaurante(restaurante);
        admin.setNombre(request.getNombre());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        String token = jwtUtil.generateToken(
            request.getEmail(),
            Rol.ADMIN.name(),
            restaurante.getId().toString()
        );

        return new LoginResponse(
            token,
            Rol.ADMIN.name(),
            request.getNombre(),
            restaurante.getId().toString(),
            null
        );
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        if (!usuario.isActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(
            usuario.getEmail(),
            usuario.getRol().name(),
            usuario.getRestaurante().getId().toString()
        );

        return new LoginResponse(
            token,
            usuario.getRol().name(),
            usuario.getNombre(),
            usuario.getRestaurante().getId().toString(),
            usuario.getRestaurante().getLogoUrl()
        );
    }

    public RestauranteDTO getPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Restaurante r = usuario.getRestaurante();
        RestauranteDTO dto = new RestauranteDTO();
        dto.setId(r.getId().toString());
        dto.setNombre(r.getNombre());
        dto.setEmail(r.getEmail());
        dto.setTelefono(r.getTelefono());
        dto.setDireccion(r.getDireccion());
        dto.setDescripcion(r.getDescripcion());
        dto.setLogoUrl(r.getLogoUrl());
        return dto;
    }
}
