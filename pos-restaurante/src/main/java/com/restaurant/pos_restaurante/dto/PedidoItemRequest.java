package com.restaurant.pos_restaurante.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoItemRequest {
    @NotNull
    private UUID productoId;

    @NotNull
    private int cantidad;

    private String notas;
}
