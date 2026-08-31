package com.backendSupermercado.supermercasdo.modules.recepcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.recepcion.entity.EstadoRecepcion;

public interface EstadoRecepcionRepository extends JpaRepository<EstadoRecepcion, Long> {

    Optional<EstadoRecepcion> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<EstadoRecepcion> findAllByActivoTrueOrderByIdEstadoRecepcionAsc();
}