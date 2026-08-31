package com.backendSupermercado.supermercasdo.modules.almacen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.TipoAlmacen;

public interface TipoAlmacenRepository extends JpaRepository<TipoAlmacen, Long> {

    List<TipoAlmacen> findByActivoTrueOrderByNombreAsc();

    Optional<TipoAlmacen> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
