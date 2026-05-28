package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.restaurant.pos_restaurante.enums.MetodoPago;

import lombok.Data;

@Data
public class VentaDTO {
    private UUID id;
    private UUID pedidoId;
    private String pedidoNumero;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private MetodoPago metodoPago;
    private LocalDateTime fecha;
}
