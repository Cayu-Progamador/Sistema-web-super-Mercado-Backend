package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Sexo;

public interface SexoRepository extends JpaRepository<Sexo, Long> {

    Optional<Sexo> findByNombreIgnoreCase(String nombre);
}
