package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;

public interface UbicacionAlmacenRepository extends JpaRepository<UbicacionAlmacen, Long>, JpaSpecificationExecutor<UbicacionAlmacen> {

    boolean existsByAlmacenAndCodigo(Almacen almacen, String codigo);
    boolean existsByAlmacenAndNombreIgnoreCase(Almacen almacen, String nombre);
    boolean existsByAlmacenAndNombreIgnoreCaseAndIdUbicacionNot(Almacen almacen, String nombre, Long idUbicacion);
    java.util.Optional<UbicacionAlmacen> findTopByOrderByIdUbicacionDesc();

    List<UbicacionAlmacen> findByAlmacen_IdAlmacenOrderByNombreAsc(Long idAlmacen);
}
