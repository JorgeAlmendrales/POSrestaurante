package com.restaurant.pos_restaurante.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CategoriaDTO {
    private UUID id;
    private String nombre;
    private int orden;
}