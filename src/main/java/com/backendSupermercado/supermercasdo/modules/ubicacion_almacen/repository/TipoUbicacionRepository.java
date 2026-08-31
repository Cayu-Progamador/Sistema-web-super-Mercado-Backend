package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.TipoUbicacion;

public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacion, Long> {
    List<TipoUbicacion> findByActivoTrueOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
}
