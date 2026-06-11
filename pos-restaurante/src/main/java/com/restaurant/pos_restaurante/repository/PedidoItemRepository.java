package com.restaurant.pos_restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restaurant.pos_restaurante.entity.*;


public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {
    @Query("""
SELECT p
FROM PedidoItem pi
JOIN pi.producto p
WHERE pi.pedido.restaurante.id = :restauranteId
GROUP BY p
ORDER BY SUM(pi.cantidad) DESC
""")
List<Producto> findProductosMasVendidos(
    @Param("restauranteId") UUID restauranteId
);
    
    List<PedidoItem> findByPedidoId(UUID pedidoId);
}
