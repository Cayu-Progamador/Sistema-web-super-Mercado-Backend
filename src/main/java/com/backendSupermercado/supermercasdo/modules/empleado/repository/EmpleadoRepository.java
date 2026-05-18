package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findById(Long id);
}
