package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;

import com.restaurant.pos_restaurante.enums.MetodoPago;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CerrarPedidoRequest {
    @NotNull
    private MetodoPago metodoPago;
    private BigDecimal descuento = BigDecimal.ZERO;
}
