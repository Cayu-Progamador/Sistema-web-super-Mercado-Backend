package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.DepartamentoGeografico;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoGeograficoRepository extends JpaRepository<DepartamentoGeografico, Long> {
    Optional<DepartamentoGeografico> findByNombreAndPais(String nombre, Pais pais);
}
