package com.backendSupermercado.supermercasdo.modules.turno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    boolean existsByNombre(String nombre);
    List<Turno> findByNombreContainingIgnoreCaseAndEstado(String nombre, Boolean estado);
    List<Turno> findByNombreContainingIgnoreCase(String nombre);
    List<Turno> findByEstado(Boolean estado);
}
