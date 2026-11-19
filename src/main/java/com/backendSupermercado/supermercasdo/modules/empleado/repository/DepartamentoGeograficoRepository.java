package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.DepartamentoGeografico;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoGeograficoRepository extends JpaRepository<DepartamentoGeografico, Long> {
    Optional<DepartamentoGeografico> findByNombreAndPais(String nombre, Pais pais);
    List<DepartamentoGeografico> findByPaisIdPaisOrderByNombreAsc(Long idPais);
}
