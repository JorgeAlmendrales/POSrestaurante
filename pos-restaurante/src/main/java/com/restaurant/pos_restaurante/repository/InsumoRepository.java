package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Insumo;
import com.restaurant.pos_restaurante.enums.EstadoInsumo;

public interface InsumoRepository extends JpaRepository<Insumo, UUID> {
    List<Insumo> findByRestauranteId(UUID restauranteId);
    List<Insumo> findByRestauranteIdAndEstado(UUID restauranteId, EstadoInsumo estado);
}
