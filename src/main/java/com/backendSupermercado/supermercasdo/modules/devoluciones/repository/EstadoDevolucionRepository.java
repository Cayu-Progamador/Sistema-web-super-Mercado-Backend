package com.backendSupermercado.supermercasdo.modules.devoluciones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.EstadoDevolucion;

public interface EstadoDevolucionRepository extends JpaRepository<EstadoDevolucion, Long> {

    Optional<EstadoDevolucion> findByNombreIgnoreCase(String nombre);

    List<EstadoDevolucion> findAllByActivoTrueOrderByIdEstadoDevolucionAsc();

    boolean existsByNombreIgnoreCase(String nombre);
}
