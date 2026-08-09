package com.backendSupermercado.supermercasdo.modules.categorias.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    long countByEstadoTrue();

    long countByEstadoFalse();

    List<Categoria> findByEstadoTrueOrderByNombreAsc();

    List<Categoria> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    @Query("""
            SELECT c FROM Categoria c
            WHERE (:patron IS NULL OR LOWER(c.nombre) LIKE :patron)
              AND (:estado IS NULL OR c.estado = :estado)
            """)
    Page<Categoria> filtrar(@Param("patron") String patron, @Param("estado") Boolean estado, Pageable pageable);
}
