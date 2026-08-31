package com.backendSupermercado.supermercasdo.modules.devoluciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DetalleOrigenDevolucion;

public interface DetalleOrigenDevolucionRepository extends JpaRepository<DetalleOrigenDevolucion, Long> {
}
