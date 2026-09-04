package com.backendSupermercado.supermercasdo.modules.inventario_fisico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.DetalleInventarioFisico;

public interface DetalleInventarioFisicoRepository extends JpaRepository<DetalleInventarioFisico, Long>,
        JpaSpecificationExecutor<DetalleInventarioFisico> {

    @Override
    @EntityGraph(attributePaths = {"producto", "producto.unidadBase", "almacen", "ubicacion", "lote"})
    Page<DetalleInventarioFisico> findAll(Specification<DetalleInventarioFisico> specification, Pageable pageable);
    long countByInventarioFisico_IdInventarioFisico(Long idInventarioFisico);

    @Query("SELECT COUNT(DISTINCT d.producto.idProducto) FROM DetalleInventarioFisico d "
            + "WHERE d.inventarioFisico.idInventarioFisico = :idInventario")
    long countProductosByInventario(@Param("idInventario") Long idInventario);

    @Query("SELECT COUNT(d) FROM DetalleInventarioFisico d WHERE d.inventarioFisico.idInventarioFisico = :idInventario "
            + "AND d.cantidadFisica IS NOT NULL")
    long countContadosByInventario(@Param("idInventario") Long idInventario);

    @Query("SELECT COUNT(d) FROM DetalleInventarioFisico d WHERE d.inventarioFisico.idInventarioFisico = :idInventario "
            + "AND d.diferencia IS NOT NULL AND d.diferencia <> 0")
    long countDiferenciasByInventario(@Param("idInventario") Long idInventario);

    @Query("SELECT MAX(d.fechaConteo) FROM DetalleInventarioFisico d "
            + "WHERE d.inventarioFisico.idInventarioFisico = :idInventario")
    java.time.LocalDateTime findUltimaFechaConteo(@Param("idInventario") Long idInventario);

    List<DetalleInventarioFisico> findByInventarioFisico_IdInventarioFisicoOrderByProducto_NombreAsc(Long idInventarioFisico);

    void deleteByInventarioFisico_IdInventarioFisico(Long idInventarioFisico);
}
