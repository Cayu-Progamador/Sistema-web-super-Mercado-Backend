package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Ciudad;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.DepartamentoGeografico;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
    Optional<Ciudad> findFirstByOrderByIdCiudadAsc();
    Optional<Ciudad> findByNombreAndDepartamento(String nombre, DepartamentoGeografico departamento);
}
