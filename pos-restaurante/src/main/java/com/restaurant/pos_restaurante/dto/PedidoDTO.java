package com.restaurant.pos_restaurante.dto;

import com.restaurant.pos_restaurante.enums.EstadoPedido;
import com.restaurant.pos_restaurante.enums.TipoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PedidoDTO {
    private UUID id;
    private String numero;
    private EstadoPedido estado;
    private TipoPedido tipo;
    private BigDecimal total;
    private String mesaNumero;
    private String usuarioNombre;
    private List<PedidoItemDTO> items;
    private LocalDateTime createdAt;
}