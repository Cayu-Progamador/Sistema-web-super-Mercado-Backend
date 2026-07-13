package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
}
