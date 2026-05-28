package com.restaurant.pos_restaurante.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.pos_restaurante.dto.CerrarPedidoRequest;
import com.restaurant.pos_restaurante.dto.VentaDTO;
import com.restaurant.pos_restaurante.entity.Pedido;
import com.restaurant.pos_restaurante.entity.Venta;
import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.repository.PedidoRepository;
import com.restaurant.pos_restaurante.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;

    public List<VentaDTO> getVentas(UUID restauranteId) {
        return ventaRepository.findByRestauranteIdOrderByFechaDesc(restauranteId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public VentaDTO cerrarPedido(UUID pedidoId, CerrarPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (pedido.getEstado() != EstadoPedido.LISTO) {
            throw new RuntimeException("El pedido debe estar en estado LISTO para cerrarlo");
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);

        BigDecimal subtotal = pedido.getTotal();
        BigDecimal descuento = request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descuento);

        Venta venta = new Venta();
        venta.setPedido(pedido);
        venta.setRestaurante(pedido.getRestaurante());
        venta.setSubtotal(subtotal);
        venta.setDescuento(descuento);
        venta.setTotal(total);
        venta.setMetodoPago(request.getMetodoPago());

        return toDTO(ventaRepository.save(venta));
    }

    private VentaDTO toDTO(Venta v) {
        VentaDTO dto = new VentaDTO();
        dto.setId(v.getId());
        dto.setPedidoId(v.getPedido().getId());
        dto.setPedidoNumero(v.getPedido().getNumero());
        dto.setSubtotal(v.getSubtotal());
        dto.setDescuento(v.getDescuento());
        dto.setTotal(v.getTotal());
        dto.setMetodoPago(v.getMetodoPago());
        dto.setFecha(v.getFecha());
        return dto;
    }
}
