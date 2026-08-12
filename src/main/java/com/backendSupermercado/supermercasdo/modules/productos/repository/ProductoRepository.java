package com.backendSupermercado.supermercasdo.modules.productos.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findBySubcategoriaIdSubcategoriaOrderByNombreAsc(Long idSubcategoria);

    long countByEstadoTrue();

    long countByEstadoFalse();

    @Query("SELECT COUNT(DISTINCT p) FROM Producto p JOIN p.presentaciones pres")
    long countConPresentaciones();

    boolean existsByNombreIgnoreCaseAndSubcategoria_IdSubcategoria(String nombre, Long idSubcategoria);

    boolean existsByNombreIgnoreCaseAndMarca_IdMarcaAndSubcategoria_IdSubcategoria(
            String nombre, Long idMarca, Long idSubcategoria);

    boolean existsByNombreIgnoreCaseAndSubcategoria_IdSubcategoriaAndIdProductoNot(
            String nombre, Long idSubcategoria, Long idProducto);

    boolean existsByNombreIgnoreCaseAndMarca_IdMarcaAndSubcategoria_IdSubcategoriaAndIdProductoNot(
            String nombre, Long idMarca, Long idSubcategoria, Long idProducto);

    boolean existsByCodigoInterno(String codigoInterno);

    @EntityGraph(attributePaths = { "subcategoria", "marca", "unidadBase" })
    @Query("""
            SELECT DISTINCT p FROM Producto p
            LEFT JOIN p.marca m
            WHERE (:patron IS NULL
                   OR LOWER(p.nombre) LIKE :patron
                   OR LOWER(COALESCE(p.codigoInterno, '')) LIKE :patron
                   OR LOWER(COALESCE(m.nombre, '')) LIKE :patron)
              AND (:estado IS NULL OR p.estado = :estado)
              AND (:idCategoria IS NULL OR p.subcategoria.categoria.id = :idCategoria)
              AND (:idSubcategoria IS NULL OR p.subcategoria.id = :idSubcategoria)
              AND (:idMarca IS NULL OR p.marca.id = :idMarca)
              AND (:idUnidadBase IS NULL OR p.unidadBase.id = :idUnidadBase)
              AND (:tienePresentaciones IS NULL
                   OR (:tienePresentaciones = true AND SIZE(p.presentaciones) > 0)
                   OR (:tienePresentaciones = false AND SIZE(p.presentaciones) = 0))
              AND (:codigoBarra IS NULL OR EXISTS (
                   SELECT cb FROM p.presentaciones pres JOIN pres.codigosBarra cb WHERE cb.codigo = :codigoBarra))
            """)
    Page<Producto> filtrar(@Param("patron") String patron,
            @Param("estado") Boolean estado,
            @Param("idCategoria") Long idCategoria,
            @Param("idSubcategoria") Long idSubcategoria,
            @Param("idMarca") Long idMarca,
            @Param("idUnidadBase") Long idUnidadBase,
            @Param("tienePresentaciones") Boolean tienePresentaciones,
            @Param("codigoBarra") String codigoBarra,
            Pageable pageable);
}
