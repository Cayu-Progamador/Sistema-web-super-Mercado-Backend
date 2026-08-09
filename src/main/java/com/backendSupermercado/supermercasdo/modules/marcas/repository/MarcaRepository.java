package com.backendSupermercado.supermercasdo.modules.marcas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.marcas.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT m FROM Marca m
            WHERE (:patron IS NULL OR LOWER(m.nombre) LIKE :patron OR LOWER(COALESCE(m.descripcion, '')) LIKE :patron)
              AND (:estado IS NULL OR m.estado = :estado)
            """)
    Page<Marca> filtrar(@Param("patron") String patron,
            @Param("estado") Boolean estado,
            Pageable pageable);
}