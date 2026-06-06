package com.restaurant.pos_restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorRequest {
    @NotBlank
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;
    private boolean activo = true;
}

