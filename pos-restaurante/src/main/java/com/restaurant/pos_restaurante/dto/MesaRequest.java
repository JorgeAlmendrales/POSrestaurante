package com.restaurant.pos_restaurante.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MesaRequest {

    @NotBlank
    private String numero;

    @Min(1)
    private int capacidad;
}