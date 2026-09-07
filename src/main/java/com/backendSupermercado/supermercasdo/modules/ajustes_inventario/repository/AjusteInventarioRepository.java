package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity.AjusteInventario;

import jakarta.persistence.LockModeType;

public interface AjusteInventarioRepository extends JpaRepository<AjusteInventario, Long>,
        JpaSpecificationExecutor<AjusteInventario> {

    @Override
    @EntityGraph(attributePaths = {"creadoPor", "aprobadoPor", "aplicadoPor", "detalles",
            "detalles.producto", "detalles.producto.unidadBase", "detalles.lote",
            "detalles.ubicacion", "detalles.ubicacion.almacen"})
    Page<AjusteInventario> findAll(Specification<AjusteInventario> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"creadoPor", "aprobadoPor", "aplicadoPor", "detalles",
            "detalles.producto", "detalles.producto.unidadBase", "detalles.lote",
            "detalles.ubicacion", "detalles.ubicacion.almacen"})
    @Query("SELECT a FROM AjusteInventario a WHERE a.idAjuste = :id")
    Optional<AjusteInventario> findDetalleById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AjusteInventario a WHERE a.idAjuste = :id")
    Optional<AjusteInventario> findByIdForUpdate(@Param("id") Long id);

    Optional<AjusteInventario> findTopByOrderByIdAjusteDesc();
}
