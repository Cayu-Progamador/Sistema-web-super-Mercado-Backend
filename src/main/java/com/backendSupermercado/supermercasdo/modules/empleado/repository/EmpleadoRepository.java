package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findById(Long id);
    @Query("""
        SELECT new com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto(
            e.idEmpleado,
            e.nombre
        )
        FROM Empleado e
        ORDER BY e.nombre ASC
    """)
    List<EmpleadoSelectDto> listarParaSelect();
}
