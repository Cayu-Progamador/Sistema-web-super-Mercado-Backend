package com.backendSupermercado.supermercasdo.modules.devoluciones.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DetalleDevolucionProveedor;

public interface DetalleDevolucionProveedorRepository extends JpaRepository<DetalleDevolucionProveedor, Long> {

    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleDevolucionProveedor d "
            + "WHERE d.detalleRecepcion.idDetalleRecepcion = :idDetalleRecepcion "
            + "AND d.devolucion.estadoDevolucion.nombre NOT IN ('BORRADOR', 'CANCELADA')")
    BigDecimal sumarDevueltoConfirmado(@Param("idDetalleRecepcion") Long idDetalleRecepcion);
}
