package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Sexo;

public interface SexoRepository extends JpaRepository<Sexo, Long> {
}
