package com.restaurant.pos_restaurante.service.adapter;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProphetResponse {
    private List<Map<String, Object>> predicciones;
    private String recomendacion;
    private double variacionPorcentaje;
}