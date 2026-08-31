package com.backendSupermercado.supermercasdo.modules.devoluciones.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.MotivoDevolucion;

public interface MotivoDevolucionRepository extends JpaRepository<MotivoDevolucion, Long> {

    Optional<MotivoDevolucion> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}