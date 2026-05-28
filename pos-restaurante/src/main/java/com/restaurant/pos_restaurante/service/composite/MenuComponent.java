package com.restaurant.pos_restaurante.service.composite;

import java.math.BigDecimal;
import java.util.List;

public interface MenuComponent {
    String getNombre();
    BigDecimal getPrecio();
    boolean isDisponible();
    List<MenuComponent> getHijos();
    int contarProductos();
}