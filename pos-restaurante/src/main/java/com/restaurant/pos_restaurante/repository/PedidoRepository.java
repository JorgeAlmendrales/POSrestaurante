package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.pos_restaurante.entity.Pedido;
import com.restaurant.pos_restaurante.enums.EstadoPedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByRestauranteIdOrderByCreatedAtDesc(UUID restauranteId);
    List<Pedido> findByRestauranteIdAndEstado(UUID restauranteId, EstadoPedido estado);
    List<Pedido> findByRestauranteIdAndEstadoNot(UUID restauranteId, EstadoPedido estado);
}
