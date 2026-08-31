package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;

public interface MovimientoAlmacenRepository extends JpaRepository<MovimientoAlmacen, Long> {

    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleMovimientoAlmacen d "
            + "WHERE d.movimiento.recepcion.id = :idRecepcion")
    BigDecimal sumarDistribuidoPorRecepcion(@Param("idRecepcion") Long idRecepcion);

    @Query("SELECT d.detalleRecepcion.id, COALESCE(SUM(d.cantidad), 0) "
            + "FROM DetalleMovimientoAlmacen d "
            + "WHERE d.movimiento.recepcion.id = :idRecepcion "
            + "AND d.detalleRecepcion IS NOT NULL "
            + "GROUP BY d.detalleRecepcion.id")
    List<Object[]> sumarDistribuidoPorDetalleRecepcion(@Param("idRecepcion") Long idRecepcion);
}
