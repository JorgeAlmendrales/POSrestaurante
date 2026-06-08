package com.restaurant.pos_restaurante.dto;

import lombok.Data;
import java.util.UUID;
import com.restaurant.pos_restaurante.enums.EstadoMesa;

@Data
public class MesaDTO {

    private UUID id;
    private String numero;
    private int capacidad;
    private EstadoMesa estado;
}