package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Mesa;
import com.restaurant.pos_restaurante.enums.EstadoMesa;

public interface MesaRepository extends JpaRepository<Mesa, UUID> {
    List<Mesa> findByRestauranteId(UUID restauranteId);
    List<Mesa> findByRestauranteIdAndEstado(UUID restauranteId, EstadoMesa estado);
}