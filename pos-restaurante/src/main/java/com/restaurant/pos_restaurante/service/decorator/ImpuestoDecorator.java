package com.restaurant.pos_restaurante.service.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ImpuestoDecorator implements PrecioCalculator {
    private final PrecioCalculator calculador;
    private final BigDecimal porcentaje;

    public ImpuestoDecorator(PrecioCalculator calculador, BigDecimal porcentaje) {
        this.calculador = calculador;
        this.porcentaje = porcentaje;
    }

    @Override
    public BigDecimal calcular(BigDecimal precioBase) {
        BigDecimal precio = calculador.calcular(precioBase);
        BigDecimal impuesto = precio.multiply(porcentaje)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return precio.add(impuesto);
    }

    @Override
    public String getDescripcion() {
        return calculador.getDescripcion() + " + IVA " + porcentaje + "%";
    }
}