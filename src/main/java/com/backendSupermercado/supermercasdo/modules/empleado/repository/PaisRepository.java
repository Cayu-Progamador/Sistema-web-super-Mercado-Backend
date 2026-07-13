package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaisRepository extends JpaRepository<Pais, Long> {
    Optional<Pais> findByNombre(String nombre);
}
