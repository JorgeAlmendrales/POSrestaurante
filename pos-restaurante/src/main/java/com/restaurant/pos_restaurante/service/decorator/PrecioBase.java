package com.restaurant.pos_restaurante.service.decorator;

import java.math.BigDecimal;

public class PrecioBase implements PrecioCalculator {
    private final BigDecimal precio;

    public PrecioBase(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public BigDecimal calcular(BigDecimal precioBase) {
        return precio;
    }

    @Override
    public String getDescripcion() {
        return "Precio base";
    }
}
