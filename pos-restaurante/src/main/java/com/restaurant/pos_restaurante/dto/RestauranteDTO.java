package com.restaurant.pos_restaurante.dto;

import lombok.Data;

@Data
public class RestauranteDTO {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private String descripcion;
    private String logoUrl;
}
