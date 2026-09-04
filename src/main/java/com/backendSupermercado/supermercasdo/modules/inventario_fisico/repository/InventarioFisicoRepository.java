package com.backendSupermercado.supermercasdo.modules.inventario_fisico.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.LockModeType;
import java.util.Optional;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.InventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;

public interface InventarioFisicoRepository extends JpaRepository<InventarioFisico, Long>,
        JpaSpecificationExecutor<InventarioFisico> {

    @Override
    @EntityGraph(attributePaths = {"responsable", "responsable.persona", "almacen", "ubicacion", "categoria"})
    Page<InventarioFisico> findAll(Specification<InventarioFisico> specification, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventarioFisico i WHERE i.idInventarioFisico = :id")
    Optional<InventarioFisico> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT COUNT(i) > 0 FROM InventarioFisico i
            WHERE i.estado IN :estados
              AND (:idExcluir IS NULL OR i.idInventarioFisico <> :idExcluir)
              AND (
                i.tipo = com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico.COMPLETO
                OR :completo = true
                OR (:idAlmacen IS NOT NULL AND i.almacen.idAlmacen = :idAlmacen)
                OR (:idUbicacion IS NOT NULL AND i.ubicacion.idUbicacion = :idUbicacion)
                OR (:idCategoria IS NOT NULL AND i.categoria.idCategoria = :idCategoria)
              )
            """)
    boolean existeConflicto(@Param("estados") Collection<EstadoInventarioFisico> estados,
            @Param("idExcluir") Long idExcluir,
            @Param("completo") boolean completo,
            @Param("idAlmacen") Long idAlmacen,
            @Param("idUbicacion") Long idUbicacion,
            @Param("idCategoria") Long idCategoria);
}
