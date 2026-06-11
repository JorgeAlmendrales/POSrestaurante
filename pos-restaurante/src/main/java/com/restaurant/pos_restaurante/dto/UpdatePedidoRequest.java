package com.restaurant.pos_restaurante.dto;

import java.util.List;
import lombok.Data;


@Data
public class UpdatePedidoRequest {

    private List<PedidoItemRequest> items;
}