package com.restaurant.pos_restaurante.dto;

import java.util.List;
import java.util.UUID;

import com.restaurant.pos_restaurante.enums.TipoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoRequest {
    @NotNull
    private TipoPedido tipo;

    private UUID mesaId;

    @NotNull
    private List<PedidoItemRequest> items;
}