package com.backendSupermercado.supermercasdo.modules.unidad_medida.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT u FROM UnidadMedida u
            WHERE (:patron IS NULL OR LOWER(u.nombre) LIKE :patron OR LOWER(u.abreviatura) LIKE :patron)
              AND (:estado IS NULL OR u.estado = :estado)
              AND (:idTipoUnidad IS NULL OR u.tipoUnidadMedida.id = :idTipoUnidad)
            """)
    Page<UnidadMedida> filtrar(@Param("patron") String patron,
            @Param("estado") Boolean estado,
            @Param("idTipoUnidad") Long idTipoUnidad,
            Pageable pageable);
}
