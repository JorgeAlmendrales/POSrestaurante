package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {
    List<Producto> findByRestauranteId(UUID restauranteId);
    List<Producto> findByRestauranteIdAndCategoriaId(UUID restauranteId, UUID categoriaId);
    List<Producto> findByRestauranteIdAndDisponible(UUID restauranteId, boolean disponible);
}
