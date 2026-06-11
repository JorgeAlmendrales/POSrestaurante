package com.restaurant.pos_restaurante.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecomendacionIADTO {

    private String producto;
    private Long demandaEstimada;

    private List<InsumoRecomendadoDTO> insumos;
}