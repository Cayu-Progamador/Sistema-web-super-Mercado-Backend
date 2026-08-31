package com.backendSupermercado.supermercasdo.modules.recepcion.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;

public interface DetalleRecepcionRepository extends JpaRepository<DetalleRecepcion, Long> {

    @Query("SELECT COALESCE(SUM(d.cantidadRecibida), 0) FROM DetalleRecepcion d "
            + "WHERE d.detallePedido.id = :idDetallePedido "
            + "AND d.recepcion.estadoRecepcion.nombre <> 'ANULADA' "
            + "AND d.recepcion.estadoRecepcion.nombre <> 'RECHAZADA'")
    BigDecimal sumarRecibidoPorDetallePedido(@Param("idDetallePedido") Long idDetallePedido);
}