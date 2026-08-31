package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;

public interface TipoMovimientoAlmacenRepository extends JpaRepository<TipoMovimientoAlmacen, Long> {

    Optional<TipoMovimientoAlmacen> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}