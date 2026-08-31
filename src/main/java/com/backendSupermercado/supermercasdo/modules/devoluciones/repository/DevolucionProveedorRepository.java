package com.backendSupermercado.supermercasdo.modules.devoluciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DevolucionProveedor;

public interface DevolucionProveedorRepository extends JpaRepository<DevolucionProveedor, Long> {

    List<DevolucionProveedor> findAllByOrderByFechaDevolucionDesc();
}
