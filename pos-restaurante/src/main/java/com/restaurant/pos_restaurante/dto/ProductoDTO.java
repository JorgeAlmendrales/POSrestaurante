package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import java.util.List;

@Data
public class ProductoDTO {

    private UUID id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private String imagenUrl;

    private boolean disponible;

    private UUID categoriaId;

    private String categoriaNombre;

    private List<RecetaIngredienteDTO> ingredientes;
}
