package com.restaurant.pos_restaurante.repository;

import com.restaurant.pos_restaurante.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

}