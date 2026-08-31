package com.backendSupermercado.supermercasdo.modules.recepcion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.recepcion.entity.RecepcionMercaderia;

public interface RecepcionMercaderiaRepository
        extends JpaRepository<RecepcionMercaderia, Long>, JpaSpecificationExecutor<RecepcionMercaderia> {

    @Query("SELECT COUNT(r) FROM RecepcionMercaderia r "
            + "WHERE r.pedido.id = :idPedido "
            + "AND r.estadoRecepcion.nombre IN ('EN_REVISION', 'PARCIAL')")
    long countEnCursoPorPedido(@Param("idPedido") Long idPedido);
}