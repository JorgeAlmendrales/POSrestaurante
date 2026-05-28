package com.restaurant.pos_restaurante.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private BigDecimal ventasDelMes;
    private int pedidosDelMes;
    private BigDecimal ticketPromedio;
    private int clientesAtendidos;
    private BigDecimal variacionVentas;
    private List<Map<String, Object>> ventasUltimos7Dias;
    private List<Map<String, Object>> topProductos;
    private List<InsumoDTO> alertasInventario;
}
