package com.backendSupermercado.supermercasdo.modules.kardex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;

public interface DetalleMovimientoAlmacenRepository
        extends JpaRepository<DetalleMovimientoAlmacen, Long>,
        JpaSpecificationExecutor<DetalleMovimientoAlmacen> {
}
