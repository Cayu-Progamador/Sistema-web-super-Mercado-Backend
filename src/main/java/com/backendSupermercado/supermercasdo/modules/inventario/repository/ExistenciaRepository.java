package com.backendSupermercado.supermercasdo.modules.inventario.repository;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;

import jakarta.persistence.LockModeType;

public interface ExistenciaRepository extends JpaRepository<Existencia, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Existencia> {

    @Override
    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "lote", "ubicacion", "ubicacion.almacen"})
    Page<Existencia> findAll(Specification<Existencia> specification, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT e.lote.idLote) FROM Existencia e "
            + "WHERE e.cantidad > 0 AND e.lote.fechaVencimiento < :fecha")
    long contarLotesVencidos(@Param("fecha") java.time.LocalDate fecha);

    @Query("SELECT COUNT(DISTINCT e.lote.idLote) FROM Existencia e "
            + "WHERE e.cantidad > 0 AND e.lote.fechaVencimiento = :fecha")
    long contarLotesQueVencenEn(@Param("fecha") java.time.LocalDate fecha);

    @Query("SELECT COUNT(DISTINCT e.lote.idLote) FROM Existencia e "
            + "WHERE e.cantidad > 0 AND e.lote.fechaVencimiento BETWEEN :desde AND :hasta")
    long contarLotesPorVencer(
            @Param("desde") java.time.LocalDate desde,
            @Param("hasta") java.time.LocalDate hasta);
    @Query("SELECT COALESCE(SUM(e.cantidad), 0) FROM Existencia e WHERE e.lote.idLote = :idLote")
    BigDecimal sumarCantidadPorLote(@Param("idLote") Long idLote);
    Optional<Existencia> findFirstByLote_IdLote(Long idLote);

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

    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "ubicacion", "ubicacion.almacen", "lote"})
    List<Existencia> findByCantidadGreaterThan(BigDecimal cantidad);

    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "ubicacion", "ubicacion.almacen", "lote"})
    List<Existencia> findByUbicacion_Almacen_IdAlmacenAndCantidadGreaterThan(Long idAlmacen, BigDecimal cantidad);

    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "ubicacion", "ubicacion.almacen", "lote"})
    List<Existencia> findByUbicacion_IdUbicacionAndCantidadGreaterThan(Long idUbicacion, BigDecimal cantidad);

    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "ubicacion", "ubicacion.almacen", "lote"})
    List<Existencia> findByProducto_Subcategoria_Categoria_IdCategoriaAndCantidadGreaterThan(
            Long idCategoria, BigDecimal cantidad);

    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "ubicacion", "ubicacion.almacen", "lote"})
    List<Existencia> findByProducto_IdProductoInAndCantidadGreaterThan(List<Long> idsProductos, BigDecimal cantidad);
}
