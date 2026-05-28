package com.restaurant.pos_restaurante.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.restaurant.pos_restaurante.enums.EstadoInsumo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "insumos")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal stockActual;

    @Column(nullable = false)
    private BigDecimal stockMinimo;

    @Column(nullable = false)
    private BigDecimal stockCritico;

    @Column(nullable = false)
    private String unidad;

    @Enumerated(EnumType.STRING)
    private EstadoInsumo estado = EstadoInsumo.NORMAL;
}
