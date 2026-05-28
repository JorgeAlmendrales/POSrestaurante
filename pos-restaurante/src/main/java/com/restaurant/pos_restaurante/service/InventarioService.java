package com.restaurant.pos_restaurante.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.pos_restaurante.dto.InsumoDTO;
import com.restaurant.pos_restaurante.dto.InsumoRequest;
import com.restaurant.pos_restaurante.entity.Insumo;
import com.restaurant.pos_restaurante.entity.MovimientoInventario;
import com.restaurant.pos_restaurante.entity.Restaurante;
import com.restaurant.pos_restaurante.enums.EstadoInsumo;
import com.restaurant.pos_restaurante.enums.TipoMovimiento;
import com.restaurant.pos_restaurante.repository.InsumoRepository;
import com.restaurant.pos_restaurante.repository.MovimientoInventarioRepository;
import com.restaurant.pos_restaurante.repository.RestauranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final RestauranteRepository restauranteRepository;

    public List<InsumoDTO> getInsumos(UUID restauranteId) {
        return insumoRepository.findByRestauranteId(restauranteId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<InsumoDTO> getAlertas(UUID restauranteId) {
        List<Insumo> bajos = insumoRepository.findByRestauranteIdAndEstado(restauranteId, EstadoInsumo.BAJO);
        List<Insumo> criticos = insumoRepository.findByRestauranteIdAndEstado(restauranteId, EstadoInsumo.CRITICO);
        bajos.addAll(criticos);
        return bajos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public InsumoDTO crearInsumo(UUID restauranteId, InsumoRequest request) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        Insumo insumo = new Insumo();
        insumo.setRestaurante(restaurante);
        insumo.setNombre(request.getNombre());
        insumo.setStockActual(request.getStockActual());
        insumo.setStockMinimo(request.getStockMinimo());
        insumo.setStockCritico(request.getStockCritico());
        insumo.setUnidad(request.getUnidad());
        insumo.setEstado(calcularEstado(request.getStockActual(), request.getStockMinimo(), request.getStockCritico()));

        return toDTO(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoDTO actualizarInsumo(UUID insumoId, InsumoRequest request) {
        Insumo insumo = insumoRepository.findById(insumoId)
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        insumo.setNombre(request.getNombre());
        insumo.setStockActual(request.getStockActual());
        insumo.setStockMinimo(request.getStockMinimo());
        insumo.setStockCritico(request.getStockCritico());
        insumo.setUnidad(request.getUnidad());
        insumo.setEstado(calcularEstado(request.getStockActual(), request.getStockMinimo(), request.getStockCritico()));

        return toDTO(insumoRepository.save(insumo));
    }

    @Transactional
    public InsumoDTO ajustarStock(UUID insumoId, BigDecimal cantidad, String motivo) {
        Insumo insumo = insumoRepository.findById(insumoId)
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        insumo.setStockActual(insumo.getStockActual().add(cantidad));
        insumo.setEstado(calcularEstado(insumo.getStockActual(), insumo.getStockMinimo(), insumo.getStockCritico()));
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setCantidad(cantidad.abs());
        movimiento.setTipo(cantidad.compareTo(BigDecimal.ZERO) > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.AJUSTE);
        movimiento.setMotivo(motivo);
        movimientoRepository.save(movimiento);

        return toDTO(insumo);
    }

    // Este método es llamado automáticamente al confirmar un pedido
    @Transactional
    public void descontarIngredientes(UUID insumoId, BigDecimal cantidad, UUID pedidoItemId) {
        Insumo insumo = insumoRepository.findById(insumoId)
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        BigDecimal nuevoStock = insumo.getStockActual().subtract(cantidad);
        if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Stock insuficiente para: " + insumo.getNombre());
        }

        insumo.setStockActual(nuevoStock);
        insumo.setEstado(calcularEstado(nuevoStock, insumo.getStockMinimo(), insumo.getStockCritico()));
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(TipoMovimiento.DESCUENTO);
        movimiento.setMotivo("Pedido confirmado");
        movimientoRepository.save(movimiento);
    }

    public EstadoInsumo calcularEstado(BigDecimal stock, BigDecimal minimo, BigDecimal critico) {
        if (stock.compareTo(critico) <= 0) return EstadoInsumo.CRITICO;
        if (stock.compareTo(minimo) <= 0) return EstadoInsumo.BAJO;
        return EstadoInsumo.NORMAL;
    }

    private InsumoDTO toDTO(Insumo i) {
        InsumoDTO dto = new InsumoDTO();
        dto.setId(i.getId());
        dto.setNombre(i.getNombre());
        dto.setStockActual(i.getStockActual());
        dto.setStockMinimo(i.getStockMinimo());
        dto.setStockCritico(i.getStockCritico());
        dto.setUnidad(i.getUnidad());
        dto.setEstado(i.getEstado());
        return dto;
    }
}