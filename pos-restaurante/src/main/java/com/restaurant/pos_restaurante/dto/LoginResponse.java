package com.restaurant.pos_restaurante.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String rol;
    private String nombre;
    private String restauranteId;
    private String logoUrl;
}
