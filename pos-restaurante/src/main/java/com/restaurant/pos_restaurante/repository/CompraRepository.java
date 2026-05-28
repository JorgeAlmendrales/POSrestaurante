package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Compra;

public interface CompraRepository extends JpaRepository<Compra, UUID> {
    List<Compra> findByRestauranteIdOrderByFechaDesc(UUID restauranteId);
}
