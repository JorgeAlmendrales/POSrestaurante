package com.restaurant.pos_restaurante.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.pos_restaurante.dto.ProveedorDTO;
import com.restaurant.pos_restaurante.dto.ProveedorRequest;
import com.restaurant.pos_restaurante.entity.Proveedor;
import com.restaurant.pos_restaurante.entity.Restaurante;
import com.restaurant.pos_restaurante.repository.ProveedorRepository;
import com.restaurant.pos_restaurante.repository.RestauranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final RestauranteRepository restauranteRepository;

    public List<ProveedorDTO> getProveedores(UUID restauranteId) {
        return proveedorRepository.findByRestauranteId(restauranteId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProveedorDTO crearProveedor(UUID restauranteId, ProveedorRequest request) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        Proveedor proveedor = new Proveedor();
        proveedor.setRestaurante(restaurante);
        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());

        return toDTO(proveedorRepository.save(proveedor));
    }

    @Transactional
    public ProveedorDTO actualizarProveedor(UUID id, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());

        return toDTO(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void eliminarProveedor(UUID id) {
        proveedorRepository.deleteById(id);
    }

    private ProveedorDTO toDTO(Proveedor p) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setContacto(p.getContacto());
        dto.setTelefono(p.getTelefono());
        dto.setEmail(p.getEmail());
        return dto;
    }
}