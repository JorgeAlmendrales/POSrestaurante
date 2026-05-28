package com.restaurant.pos_restaurante.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.pos_restaurante.dto.CategoriaDTO;
import com.restaurant.pos_restaurante.dto.ProductoDTO;
import com.restaurant.pos_restaurante.dto.ProductoRequest;
import com.restaurant.pos_restaurante.dto.RecetaIngredienteDTO;
import com.restaurant.pos_restaurante.entity.Categoria;
import com.restaurant.pos_restaurante.entity.Insumo;
import com.restaurant.pos_restaurante.entity.Producto;
import com.restaurant.pos_restaurante.entity.RecetaIngrediente;
import com.restaurant.pos_restaurante.entity.Restaurante;
import com.restaurant.pos_restaurante.repository.CategoriaRepository;
import com.restaurant.pos_restaurante.repository.InsumoRepository;
import com.restaurant.pos_restaurante.repository.ProductoRepository;
import com.restaurant.pos_restaurante.repository.RecetaIngredienteRepository;
import com.restaurant.pos_restaurante.repository.RestauranteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final RestauranteRepository restauranteRepository;
    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final InsumoRepository insumoRepository;

    public List<CategoriaDTO> getCategorias(UUID restauranteId) {
        return categoriaRepository.findByRestauranteIdOrderByOrden(restauranteId)
            .stream().map(this::toCategoriaDTO).collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDTO crearCategoria(UUID restauranteId, String nombre) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        Categoria categoria = new Categoria();
        categoria.setRestaurante(restaurante);
        categoria.setNombre(nombre);
        categoria.setOrden(categoriaRepository.findByRestauranteIdOrderByOrden(restauranteId).size() + 1);
        return toCategoriaDTO(categoriaRepository.save(categoria));
    }

    public List<ProductoDTO> getProductos(UUID restauranteId, UUID categoriaId) {
        List<Producto> productos = categoriaId != null
            ? productoRepository.findByRestauranteIdAndCategoriaId(restauranteId, categoriaId)
            : productoRepository.findByRestauranteId(restauranteId);
        return productos.stream().map(this::toProductoDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProductoDTO crearProducto(UUID restauranteId, ProductoRequest request) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        Producto producto = new Producto();
        producto.setRestaurante(restaurante);
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setDisponible(request.isDisponible());

        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                .ifPresent(producto::setCategoria);
        }

        return toProductoDTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO actualizarProducto(UUID productoId, ProductoRequest request) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setDisponible(request.isDisponible());

        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId())
                .ifPresent(producto::setCategoria);
        }

        return toProductoDTO(productoRepository.save(producto));
    }

    @Transactional
    public void eliminarProducto(UUID productoId) {
        recetaIngredienteRepository.deleteByProductoId(productoId);
        productoRepository.deleteById(productoId);
    }

    public List<RecetaIngredienteDTO> getReceta(UUID productoId) {
        return recetaIngredienteRepository.findByProductoId(productoId)
            .stream().map(this::toRecetaDTO).collect(Collectors.toList());
    }

    @Transactional
    public RecetaIngredienteDTO agregarIngrediente(UUID productoId, RecetaIngredienteDTO dto) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Insumo insumo = insumoRepository.findById(dto.getInsumoId())
            .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

        RecetaIngrediente ingrediente = new RecetaIngrediente();
        ingrediente.setProducto(producto);
        ingrediente.setInsumo(insumo);
        ingrediente.setCantidad(dto.getCantidad());
        ingrediente.setUnidad(dto.getUnidad());

        return toRecetaDTO(recetaIngredienteRepository.save(ingrediente));
    }

    @Transactional
    public void eliminarIngrediente(UUID ingredienteId) {
        recetaIngredienteRepository.deleteById(ingredienteId);
    }

    private CategoriaDTO toCategoriaDTO(Categoria c) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setOrden(c.getOrden());
        return dto;
    }

    private ProductoDTO toProductoDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setDisponible(p.isDisponible());
        if (p.getCategoria() != null) {
            dto.setCategoriaId(p.getCategoria().getId());
            dto.setCategoriaNombre(p.getCategoria().getNombre());
        }
        return dto;
    }

    private RecetaIngredienteDTO toRecetaDTO(RecetaIngrediente r) {
        RecetaIngredienteDTO dto = new RecetaIngredienteDTO();
        dto.setId(r.getId());
        dto.setInsumoId(r.getInsumo().getId());
        dto.setInsumoNombre(r.getInsumo().getNombre());
        dto.setCantidad(r.getCantidad());
        dto.setUnidad(r.getUnidad());
        return dto;
    }
}
