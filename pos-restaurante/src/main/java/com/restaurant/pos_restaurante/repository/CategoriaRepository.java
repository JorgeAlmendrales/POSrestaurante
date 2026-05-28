package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {
    List<Categoria> findByRestauranteIdOrderByOrden(UUID restauranteId);
}
