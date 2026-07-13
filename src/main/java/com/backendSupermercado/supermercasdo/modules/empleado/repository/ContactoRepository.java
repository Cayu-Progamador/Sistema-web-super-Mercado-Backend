package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
}
