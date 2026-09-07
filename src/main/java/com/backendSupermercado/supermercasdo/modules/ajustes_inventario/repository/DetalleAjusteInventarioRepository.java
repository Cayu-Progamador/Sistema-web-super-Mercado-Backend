package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity.DetalleAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;

import jakarta.persistence.LockModeType;

public interface DetalleAjusteInventarioRepository extends JpaRepository<DetalleAjusteInventario, Long> {

    Optional<DetalleAjusteInventario> findFirstByProducto_IdProductoAndLote_IdLoteAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
            Long idProducto, Long idLote, EstadoAjusteInventario estado);

    Optional<DetalleAjusteInventario> findFirstByProducto_IdProductoAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
            Long idProducto, EstadoAjusteInventario estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DetalleAjusteInventario d WHERE d.idDetalle = :id")
    Optional<DetalleAjusteInventario> findByIdForUpdate(@Param("id") Long id);
}
