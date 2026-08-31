package com.backendSupermercado.supermercasdo.modules.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;

public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {

    @EntityGraph(attributePaths = { "unidadMedida", "producto" })
    @Query("""
            SELECT p FROM PresentacionProducto p
            WHERE p.producto.id = :idProducto
              AND p.unidadMedida.id = :idUnidadMedida
              AND p.estado = true
            """)
    Optional<PresentacionProducto> findByProductoIdAndUnidadMedidaId(
            @Param("idProducto") Long idProducto,
            @Param("idUnidadMedida") Long idUnidadMedida);

    @EntityGraph(attributePaths = { "unidadMedida" })
    @Query("""
            SELECT p FROM PresentacionProducto p
            WHERE p.producto.id = :idProducto
              AND p.estado = true
            ORDER BY p.esPrincipal DESC
            """)
    List<PresentacionProducto> findAllByProductoId(@Param("idProducto") Long idProducto);
}