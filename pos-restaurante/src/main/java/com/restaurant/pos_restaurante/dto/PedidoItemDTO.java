package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class PedidoItemDTO {
    private UUID id;
    private UUID productoId;
    private String productoNombre;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String notas;
}
