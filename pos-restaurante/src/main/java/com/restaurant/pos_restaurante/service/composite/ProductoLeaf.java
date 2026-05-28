package com.restaurant.pos_restaurante.service.composite;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ProductoLeaf implements MenuComponent {
    private final String nombre;
    private final BigDecimal precio;
    private final boolean disponible;

    public ProductoLeaf(String nombre, BigDecimal precio, boolean disponible) {
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    @Override public String getNombre() { return nombre; }
    @Override public BigDecimal getPrecio() { return precio; }
    @Override public boolean isDisponible() { return disponible; }
    @Override public List<MenuComponent> getHijos() { return Collections.emptyList(); }
    @Override public int contarProductos() { return 1; }
}
