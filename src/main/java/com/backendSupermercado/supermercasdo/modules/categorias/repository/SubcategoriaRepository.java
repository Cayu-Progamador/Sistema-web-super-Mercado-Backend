package com.backendSupermercado.supermercasdo.modules.categorias.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.categorias.entity.Subcategoria;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("""
            SELECT s FROM Subcategoria s
            WHERE (:idCategoria IS NULL OR s.categoria.id = :idCategoria)
              AND s.categoria.estado = true
            ORDER BY s.nombre ASC
            """)
    List<Subcategoria> findByCategoriaActivaOrderByNombreAsc(@Param("idCategoria") Long idCategoria);

    long countByCategoria_IdCategoria(Long idCategoria);
}
