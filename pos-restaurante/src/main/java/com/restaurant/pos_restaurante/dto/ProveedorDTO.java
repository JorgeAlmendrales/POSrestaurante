package com.restaurant.pos_restaurante.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ProveedorDTO {
    private UUID id;
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;
}