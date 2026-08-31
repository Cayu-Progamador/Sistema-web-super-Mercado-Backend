package com.backendSupermercado.supermercasdo.modules.inventario.repository;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;

import jakarta.persistence.LockModeType;

public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {

    boolean existsByUbicacion_Almacen_IdAlmacenAndCantidadGreaterThan(Long idAlmacen, BigDecimal cantidad);
    List<Existencia> findByUbicacion_IdUbicacionIn(List<Long> idsUbicacion);

    @EntityGraph(attributePaths = {
            "ubicacion", "ubicacion.almacen", "producto", "producto.subcategoria",
            "producto.subcategoria.categoria", "producto.marca", "producto.unidadBase",
            "producto.presentaciones", "lote"
    })
    @Query("SELECT DISTINCT e FROM Existencia e " +
            "WHERE e.ubicacion.almacen.idAlmacen = :idAlmacen AND e.cantidad > 0 " +
            "ORDER BY e.ubicacion.nombre, e.producto.nombre, e.lote.fechaVencimiento")
    List<Existencia> listarInventarioParaReporte(@Param("idAlmacen") Long idAlmacen);

    Optional<Existencia> findByProducto_IdProductoAndUbicacion_IdUbicacionAndLote_IdLote(Long idProducto, Long idUbicacion, Long idLote);

    Optional<Existencia> findByProducto_IdProductoAndUbicacion_IdUbicacionAndLoteIsNull(Long idProducto, Long idUbicacion);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Existencia e WHERE e.producto.idProducto = :idProducto "
            + "AND e.ubicacion.idUbicacion = :idUbicacion "
            + "AND ((:idLote IS NULL AND e.lote IS NULL) OR e.lote.idLote = :idLote)")
    Optional<Existencia> buscarParaActualizar(@Param("idProducto") Long idProducto,
            @Param("idUbicacion") Long idUbicacion, @Param("idLote") Long idLote);

    List<Existencia> findAllByProducto_IdProductoAndLote_IdLoteAndCantidadGreaterThan(
            Long idProducto, Long idLote, BigDecimal cantidad);

    List<Existencia> findAllByProducto_IdProductoAndLoteIsNullAndCantidadGreaterThan(
            Long idProducto, BigDecimal cantidad);
}
