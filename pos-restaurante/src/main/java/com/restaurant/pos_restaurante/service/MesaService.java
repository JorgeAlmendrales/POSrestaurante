package com.restaurant.pos_restaurante.service;

import com.restaurant.pos_restaurante.dto.MesaDTO;
import com.restaurant.pos_restaurante.entity.Mesa;
import com.restaurant.pos_restaurante.enums.EstadoMesa;
import com.restaurant.pos_restaurante.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import com.restaurant.pos_restaurante.dto.MesaRequest;
import com.restaurant.pos_restaurante.entity.Restaurante;
import com.restaurant.pos_restaurante.repository.RestauranteRepository;
import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.repository.PedidoRepository;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final RestauranteRepository restauranteRepository;
    private final PedidoRepository pedidoRepository;

    public List<MesaDTO> getDisponibles(UUID restauranteId) {

        return mesaRepository
            .findByRestauranteIdAndEstado(
                restauranteId,
                EstadoMesa.DISPONIBLE
            )
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Transactional
public MesaDTO crearMesa(
    UUID restauranteId,
    MesaRequest request
) {

    Restaurante restaurante =
        restauranteRepository.findById(restauranteId)
        .orElseThrow(() ->
            new RuntimeException("Restaurante no encontrado"));

    Mesa mesa = new Mesa();

    mesa.setRestaurante(restaurante);
    mesa.setNumero(request.getNumero());
    mesa.setCapacidad(request.getCapacidad());
    mesa.setEstado(EstadoMesa.DISPONIBLE);

    return toDTO(
        mesaRepository.save(mesa)
    );
}

public List<MesaDTO> getMesas(UUID restauranteId) {

    return mesaRepository
        .findByRestauranteId(restauranteId)
        .stream()
        .map(this::toDTO)
        .toList();
}

@Transactional
public MesaDTO actualizarMesa(
    UUID mesaId,
    MesaRequest request
) {

    Mesa mesa = mesaRepository.findById(mesaId)
        .orElseThrow(() ->
            new RuntimeException("Mesa no encontrada"));

    mesa.setNumero(request.getNumero());
    mesa.setCapacidad(request.getCapacidad());

    return toDTO(
        mesaRepository.save(mesa)
    );
}

@Transactional
public void eliminarMesa(UUID mesaId) {

    Mesa mesa = mesaRepository.findById(mesaId)
        .orElseThrow(() ->
            new RuntimeException("Mesa no encontrada"));

    if (mesa.getEstado() == EstadoMesa.OCUPADA) {

        throw new RuntimeException(
            "No se puede eliminar una mesa ocupada"
        );
    }

    mesaRepository.delete(mesa);
}

    private MesaDTO toDTO(Mesa mesa) {

        MesaDTO dto = new MesaDTO();

        dto.setId(mesa.getId());
        dto.setNumero(mesa.getNumero());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setEstado(mesa.getEstado());

        return dto;
    }
}