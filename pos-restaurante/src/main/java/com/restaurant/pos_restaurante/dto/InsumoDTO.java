package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.restaurant.pos_restaurante.enums.EstadoInsumo;

import lombok.Data;

@Data
public class InsumoDTO {
    private UUID id;
    private String nombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal stockCritico;
    private String unidad;
    private EstadoInsumo estado;
}
