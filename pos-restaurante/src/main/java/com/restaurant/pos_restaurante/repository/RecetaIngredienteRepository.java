package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.RecetaIngrediente;

public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, UUID> {
    List<RecetaIngrediente> findByProductoId(UUID productoId);
    void deleteByProductoId(UUID productoId);
}
