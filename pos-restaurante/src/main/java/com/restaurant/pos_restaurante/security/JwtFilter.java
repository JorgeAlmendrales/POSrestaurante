package com.restaurant.pos_restaurante.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.restaurant.pos_restaurante.entity.Usuario;
import com.restaurant.pos_restaurante.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("\n=================================");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Authorization: " + authHeader);
        System.out.println("=================================\n");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            try {

                String token = authHeader.substring(7);

                System.out.println("TOKEN RECIBIDO:");
                System.out.println(token);

                boolean tokenValido = jwtUtil.isTokenValid(token);

                System.out.println("TOKEN VALIDO: " + tokenValido);

                if (tokenValido) {

                    String email = jwtUtil.extractEmail(token);
                    String rol = jwtUtil.extractRol(token);

                    System.out.println("EMAIL TOKEN: " + email);
                    System.out.println("ROL TOKEN: " + rol);

                    Usuario usuario = usuarioRepository
                            .findByEmail(email)
                            .orElse(null);

                    System.out.println("USUARIO BD: " + usuario);

                    if (usuario != null) {

                        System.out.println(
                                "Usuario autenticado: "
                                        + usuario.getEmail());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        usuario,
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_" + rol)));

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);

                        System.out.println("AUTHENTICATION REGISTRADA");
                    } else {

                        System.out.println(
                                "ERROR: Usuario no encontrado en BD");
                    }

                } else {

                    System.out.println(
                            "ERROR: Token inválido o expirado");
                }

            } catch (Exception e) {

                System.out.println("ERROR JWT:");
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}