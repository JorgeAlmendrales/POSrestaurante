package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class InsumoRequest {

    @NotBlank
    private String nombre;

    @NotNull
    private BigDecimal stockActual;

    @NotNull
    private BigDecimal stockMinimo;

    @NotNull
    private BigDecimal stockCritico;

    @NotBlank
    private String unidad;

    private UUID proveedorId;
}
