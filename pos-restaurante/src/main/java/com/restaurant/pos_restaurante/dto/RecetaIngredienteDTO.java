package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecetaIngredienteDTO {
    private UUID id;

    @NotNull
    private UUID insumoId;

    private String insumoNombre;

    @NotNull
    private BigDecimal cantidad;

    @NotNull
    private String unidad;
}
