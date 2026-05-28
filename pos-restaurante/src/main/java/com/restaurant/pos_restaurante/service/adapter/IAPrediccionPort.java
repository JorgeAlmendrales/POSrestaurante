package com.restaurant.pos_restaurante.service.adapter;

import java.util.List;
import java.util.Map;

public interface IAPrediccionPort {
    ProphetResponse predecir(List<Map<String, Object>> datosVentas);
}