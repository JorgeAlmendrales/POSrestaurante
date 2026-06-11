package com.restaurant.pos_restaurante.service;

import com.restaurant.pos_restaurante.dto.*;
import com.restaurant.pos_restaurante.entity.*;
import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.enums.EstadoMesa;
import com.restaurant.pos_restaurante.enums.TipoPedido;
import com.restaurant.pos_restaurante.repository.ClienteRepository;
import com.restaurant.pos_restaurante.dto.UpdatePedidoRequest;
import com.restaurant.pos_restaurante.enums.MetodoPago;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProductoRepository productoRepository;
    private final MesaRepository mesaRepository;
    private final RestauranteRepository restauranteRepository;
    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final InventarioService inventarioService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    public List<PedidoDTO> getPedidos(UUID restauranteId) {
        return pedidoRepository.findByRestauranteIdOrderByCreatedAtDesc(restauranteId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PedidoDTO> getPedidosActivos(UUID restauranteId) {

    return pedidoRepository
        .findByRestauranteIdAndEstadoIn(
            restauranteId,
            List.of(
                EstadoPedido.NUEVO,
                EstadoPedido.EN_PREPARACION,
                EstadoPedido.LISTO
            )
        )
        .stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
}

    @Transactional
    public PedidoDTO crearPedido(UUID restauranteId, UUID usuarioId, PedidoRequest request) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new RuntimeException("Restaurante no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setRestaurante(restaurante);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        pedido.setUsuario(usuario);

        pedido.setTipo(request.getTipo());
        pedido.setEstado(EstadoPedido.NUEVO);
        pedido.setNumero("PED-" + System.currentTimeMillis());

        if (request.getMesaId() != null) {

    Mesa mesa = mesaRepository.findById(request.getMesaId())
        .orElseThrow(() ->
            new RuntimeException("Mesa no encontrada"));

    if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
        throw new RuntimeException(
            "La mesa no está disponible"
        );
    }

    mesa.setEstado(EstadoMesa.OCUPADA);

    pedido.setMesa(mesa);
}

        pedido = pedidoRepository.save(pedido);

        List<PedidoItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemRequest itemReq : request.getItems()) {
            Producto producto = productoRepository.findById(itemReq.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemReq.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            item.setNotas(itemReq.getNotas());
            items.add(pedidoItemRepository.save(item));

            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(itemReq.getCantidad())));
        }

        Cliente cliente = null;

if (request.getTipo() != TipoPedido.MESA) {

    cliente = new Cliente();

    cliente.setNombre(request.getClienteNombre());
    cliente.setTelefono(request.getClienteTelefono());
    cliente.setDireccion(request.getClienteDireccion());

    cliente = clienteRepository.save(cliente);

    pedido.setCliente(cliente);
}

        pedido.setTotal(total);
        pedido.setItems(items);
        pedidoRepository.save(pedido);

        PedidoDTO dto = toDTO(pedido);

        // Notificar por WebSocket a todos los conectados
        messagingTemplate.convertAndSend("/topic/pedidos/" + restauranteId, dto);

        return dto;
    }

    @Transactional
public PedidoDTO actualizarPedido(
        UUID pedidoId,
        UpdatePedidoRequest request
) {

    Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() ->
            new RuntimeException("Pedido no encontrado")
        );

    if (
        // Solo se permiten cambios en pedidos NUEVOS, EN_PREPARACION o LISTOS
        pedido.getEstado() != EstadoPedido.NUEVO && pedido.getEstado() != EstadoPedido.LISTO && pedido.getEstado() != EstadoPedido.EN_PREPARACION
    ) {
        throw new RuntimeException(
            "Solo se pueden modificar pedidos en estado NUEVO"
        );
    }

    pedidoItemRepository.deleteAll(
        pedido.getItems()
    );

    List<PedidoItem> nuevosItems = new ArrayList<>();

    BigDecimal total = BigDecimal.ZERO;

    for (PedidoItemRequest itemReq : request.getItems()) {

        Producto producto =
            productoRepository.findById(
                itemReq.getProductoId()
            )
            .orElseThrow(() ->
                new RuntimeException(
                    "Producto no encontrado"
                )
            );

        PedidoItem item = new PedidoItem();

        item.setPedido(pedido);
        item.setProducto(producto);
        item.setCantidad(itemReq.getCantidad());
        item.setPrecioUnitario(
            producto.getPrecio()
        );
        item.setNotas(itemReq.getNotas());

        nuevosItems.add(
            pedidoItemRepository.save(item)
        );

        total = total.add(
            producto.getPrecio()
                .multiply(
                    BigDecimal.valueOf(
                        itemReq.getCantidad()
                    )
                )
        );
    }

    pedido.setItems(nuevosItems);
    pedido.setTotal(total);

    pedidoRepository.save(pedido);

    PedidoDTO dto = toDTO(pedido);

    messagingTemplate.convertAndSend(
        "/topic/pedidos/" +
        pedido.getRestaurante().getId(),
        dto
    );

    return dto;
}

    @Transactional
public PedidoDTO cambiarEstado(UUID pedidoId, EstadoPedido nuevoEstado) {


    Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() ->
            new RuntimeException("Pedido no encontrado"));

    EstadoPedido estadoActual = pedido.getEstado();

boolean valido = switch (estadoActual) {

    case NUEVO ->
        nuevoEstado == EstadoPedido.EN_PREPARACION
        || nuevoEstado == EstadoPedido.CANCELADO;

    case EN_PREPARACION ->
        nuevoEstado == EstadoPedido.LISTO
        || nuevoEstado == EstadoPedido.CANCELADO;

    case LISTO ->
        nuevoEstado == EstadoPedido.ENTREGADO;

    case ENTREGADO, CANCELADO ->
        false;
};

if (!valido) {
    throw new RuntimeException(
        "Cambio de estado no permitido"
    );
}
    
    pedido.setEstado(nuevoEstado);

    if (
    nuevoEstado == EstadoPedido.ENTREGADO &&
    !ventaRepository.existsByPedidoId(pedido.getId())
) {

    Venta venta = new Venta();

    venta.setPedido(pedido);
    venta.setRestaurante(pedido.getRestaurante());

    venta.setSubtotal(pedido.getTotal());
    venta.setDescuento(BigDecimal.ZERO);
    venta.setTotal(pedido.getTotal());

    venta.setMetodoPago(MetodoPago.EFECTIVO);

    ventaRepository.save(venta);
}

    // Liberar mesa cuando termina o se cancela
    if (
        (nuevoEstado == EstadoPedido.ENTREGADO ||
         nuevoEstado == EstadoPedido.CANCELADO)
        &&
        pedido.getMesa() != null
    ) {

        Mesa mesa = pedido.getMesa();

        mesa.setEstado(EstadoMesa.DISPONIBLE);

        mesaRepository.save(mesa);
    }

    // Descontar inventario al iniciar preparación
    if (nuevoEstado == EstadoPedido.EN_PREPARACION) {

        for (PedidoItem item : pedido.getItems()) {

            List<RecetaIngrediente> ingredientes =
                recetaIngredienteRepository.findByProductoId(
                    item.getProducto().getId()
                );

            for (RecetaIngrediente ingrediente : ingredientes) {

                BigDecimal cantidadTotal =
                    ingrediente.getCantidad()
                        .multiply(
                            BigDecimal.valueOf(
                                item.getCantidad()
                            )
                        );

                inventarioService.descontarIngredientes(
                    ingrediente.getInsumo().getId(),
                    cantidadTotal,
                    item.getId()
                );
            }
        }
    }

    pedidoRepository.save(pedido);

    PedidoDTO dto = toDTO(pedido);

    messagingTemplate.convertAndSend(
        "/topic/pedidos/" +
        pedido.getRestaurante().getId(),
        dto
    );

    return dto;
}

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId());
        dto.setNumero(p.getNumero());
        dto.setEstado(p.getEstado());
        dto.setTipo(p.getTipo());
        dto.setTotal(p.getTotal());
        dto.setCreatedAt(p.getCreatedAt());

        if (p.getMesa() != null) dto.setMesaNumero(p.getMesa().getNumero());
        if (p.getUsuario() != null) dto.setUsuarioNombre(p.getUsuario().getNombre());

        if (p.getItems() != null) {
            dto.setItems(p.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()));
        }
        if (p.getCliente() != null) {

    dto.setClienteNombre(
        p.getCliente().getNombre()
    );

    dto.setClienteTelefono(
        p.getCliente().getTelefono()
    );

    dto.setClienteDireccion(
        p.getCliente().getDireccion()
    );
}

        return dto;
    }

    private PedidoItemDTO toItemDTO(PedidoItem i) {
        PedidoItemDTO dto = new PedidoItemDTO();
        dto.setId(i.getId());
        dto.setProductoId(i.getProducto().getId());
        dto.setProductoNombre(i.getProducto().getNombre());
        dto.setCantidad(i.getCantidad());
        dto.setPrecioUnitario(i.getPrecioUnitario());
        dto.setSubtotal(i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())));
        dto.setNotas(i.getNotas());
        return dto;
    }
}