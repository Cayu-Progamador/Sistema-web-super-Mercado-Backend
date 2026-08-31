package com.backendSupermercado.supermercasdo.modules.almacen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenMetricasDto;
import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long>, JpaSpecificationExecutor<Almacen> {

    Optional<Almacen> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdAlmacenNot(String nombre, Long idAlmacen);

    @Query("SELECT new com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenMetricasDto(" +
            "a.idAlmacen, COUNT(DISTINCT u.idUbicacion), " +
            "COUNT(DISTINCT CASE WHEN e.cantidad > 0 THEN e.producto.idProducto ELSE NULL END), " +
            "COALESCE(SUM(e.cantidad), 0)) " +
            "FROM Almacen a " +
            "LEFT JOIN a.ubicaciones u " +
            "LEFT JOIN Existencia e ON e.ubicacion = u " +
            "WHERE a.idAlmacen IN :ids " +
            "GROUP BY a.idAlmacen")
    List<AlmacenMetricasDto> obtenerMetricasPorIds(@Param("ids") List<Long> ids);
}
