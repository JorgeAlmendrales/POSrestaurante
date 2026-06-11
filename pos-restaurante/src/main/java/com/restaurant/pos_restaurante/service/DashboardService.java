package com.restaurant.pos_restaurante.service;

import com.restaurant.pos_restaurante.dto.DashboardDTO;
import com.restaurant.pos_restaurante.dto.RecomendacionIADTO;
import com.restaurant.pos_restaurante.dto.InsumoRecomendadoDTO;
import com.restaurant.pos_restaurante.dto.InsumoDTO;
import com.restaurant.pos_restaurante.entity.PedidoItem;
import com.restaurant.pos_restaurante.entity.Venta;
import com.restaurant.pos_restaurante.entity.Producto;
import com.restaurant.pos_restaurante.entity.RecetaIngrediente;
import com.restaurant.pos_restaurante.enums.EstadoInsumo;
import com.restaurant.pos_restaurante.repository.*;
import com.restaurant.pos_restaurante.repository.RecetaIngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioService inventarioService;
    private final RecetaIngredienteRepository recetaIngredienteRepository;
    private final IAService iaService;

    public RecomendacionIADTO getRecomendacionIA(UUID restauranteId) {

    List<Producto> productos =
        pedidoItemRepository.findProductosMasVendidos(restauranteId);

    if (productos.isEmpty()) {
        return null;
    }

    Producto producto = productos.get(0);

    List<RecetaIngrediente> receta =
        recetaIngredienteRepository.findByProductoId(
            producto.getId()
        );

    RecomendacionIADTO dto =
        new RecomendacionIADTO();

    dto.setProducto(producto.getNombre());

    Map<String, Object> respuestaIA =
    iaService.obtenerRecomendacionProducto(
        producto.getId().toString()
    );

    System.out.println("Respuesta IA:");
System.out.println(respuestaIA);

long demandaEstimada = 0L;

if (
    respuestaIA != null &&
    !respuestaIA.containsKey("error")
) {

    demandaEstimada =
        ((Number) respuestaIA.get("demanda"))
            .longValue();
}

dto.setDemandaEstimada(demandaEstimada);

    List<InsumoRecomendadoDTO> insumos =
        new ArrayList<>();

    for (RecetaIngrediente ingrediente : receta) {

        InsumoRecomendadoDTO item =
            new InsumoRecomendadoDTO();

        item.setNombre(
            ingrediente.getInsumo().getNombre()
        );

        item.setUnidad(
            ingrediente.getUnidad()
        );

        item.setCantidad(
            ingrediente.getCantidad()
                .doubleValue() *
            demandaEstimada
        );

        insumos.add(item);
    }

    dto.setInsumos(insumos);

    return dto;
}
    
    public DashboardDTO getDashboard(UUID restauranteId) {
        DashboardDTO dto = new DashboardDTO();

        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime ahora = LocalDateTime.now();

        // Ventas del mes
        List<Venta> ventasMes = ventaRepository.findByRestauranteIdAndFechaBetween(
            restauranteId, inicioMes, ahora);

        BigDecimal totalVentas = ventasMes.stream()
            .map(Venta::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setVentasDelMes(totalVentas);
        dto.setPedidosDelMes(ventasMes.size());

        // Ticket promedio
        if (!ventasMes.isEmpty()) {
            dto.setTicketPromedio(totalVentas.divide(
                BigDecimal.valueOf(ventasMes.size()), 2, RoundingMode.HALF_UP));
        } else {
            dto.setTicketPromedio(BigDecimal.ZERO);
        }

        // Clientes atendidos (pedidos únicos)
        dto.setClientesAtendidos(ventasMes.size());

        // Ventas últimos 7 días
        List<Map<String, Object>> ventas7Dias = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime dia = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime finDia = dia.withHour(23).withMinute(59).withSecond(59);

            List<Venta> ventasDia = ventaRepository.findByRestauranteIdAndFechaBetween(
                restauranteId, dia, finDia);

            BigDecimal totalDia = ventasDia.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> punto = new HashMap<>();
            punto.put("dia", dia.getDayOfWeek().toString().substring(0, 3));
            punto.put("total", totalDia);
            ventas7Dias.add(punto);
        }
        dto.setVentasUltimos7Dias(ventas7Dias);

        // Top 5 productos más vendidos
        List<PedidoItem> todosItems = pedidoItemRepository.findAll();
        Map<String, Long> conteo = todosItems.stream()
            .filter(item -> item.getPedido().getRestaurante().getId().equals(restauranteId))
            .collect(Collectors.groupingBy(
                item -> item.getProducto().getNombre(),
                Collectors.summingLong(PedidoItem::getCantidad)
            ));

        List<Map<String, Object>> topProductos = conteo.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                Map<String, Object> p = new HashMap<>();
                p.put("nombre", entry.getKey());
                p.put("cantidad", entry.getValue());
                return p;
            })
            .collect(Collectors.toList());
        dto.setTopProductos(topProductos);

        // Alertas de inventario
        List<InsumoDTO> alertas = inventarioService.getAlertas(restauranteId);
        dto.setAlertasInventario(alertas);

        return dto;
    }
}