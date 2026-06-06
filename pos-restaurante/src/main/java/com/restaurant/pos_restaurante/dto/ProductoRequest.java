package com.restaurant.pos_restaurante.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ProductoRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private BigDecimal precio;

    private UUID categoriaId;

    private boolean disponible = true;

    private String imagenUrl;

    private List<RecetaIngredienteDTO> ingredientes;
}
