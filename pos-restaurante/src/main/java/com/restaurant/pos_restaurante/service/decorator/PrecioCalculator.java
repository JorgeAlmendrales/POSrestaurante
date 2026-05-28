package com.restaurant.pos_restaurante.service.decorator;

import java.math.BigDecimal;

public interface PrecioCalculator {
    BigDecimal calcular(BigDecimal precioBase);
    String getDescripcion();
}
