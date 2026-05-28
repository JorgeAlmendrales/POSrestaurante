package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.CompraDetalle;

public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, UUID> {
    List<CompraDetalle> findByCompraId(UUID compraId);
}