package com.restaurant.pos_restaurante.service.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CategoriaComposite implements MenuComponent {
    private final String nombre;
    private final List<MenuComponent> hijos = new ArrayList<>();

    public CategoriaComposite(String nombre) {
        this.nombre = nombre;
    }

    public void agregar(MenuComponent componente) {
        hijos.add(componente);
    }

    @Override public String getNombre() { return nombre; }

    @Override
    public BigDecimal getPrecio() {
        return hijos.stream()
            .map(MenuComponent::getPrecio)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean isDisponible() {
        return hijos.stream().anyMatch(MenuComponent::isDisponible);
    }

    @Override public List<MenuComponent> getHijos() { return hijos; }

    @Override
    public int contarProductos() {
        return hijos.stream().mapToInt(MenuComponent::contarProductos).sum();
    }
}