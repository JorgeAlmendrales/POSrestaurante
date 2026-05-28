package com.restaurant.pos_restaurante.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restaurant.pos_restaurante.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, UUID> {
    List<Venta> findByRestauranteIdOrderByFechaDesc(UUID restauranteId);

    @Query("SELECT v FROM Venta v WHERE v.restaurante.id = :restauranteId AND v.fecha BETWEEN :inicio AND :fin")
    List<Venta> findByRestauranteIdAndFechaBetween(
        @Param("restauranteId") UUID restauranteId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}
