package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long>, JpaSpecificationExecutor<Empleado> {

    Optional<Empleado> findById(Long id);
    @Query("""
        SELECT new com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto(
            e.idEmpleado,
            e.persona.nombres
        )
        FROM Empleado e
        WHERE e.idEmpleado NOT IN (
            SELECT u.empleado.idEmpleado FROM Usuario u WHERE u.empleado IS NOT NULL
        )
        ORDER BY e.persona.nombres ASC
    """)
    List<EmpleadoSelectDto> listarParaSelect();

    @Query("""
        SELECT new com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto(
            e.idEmpleado,
            e.persona.nombres
        )
        FROM Empleado e
        WHERE e.idEmpleado NOT IN (
            SELECT u.empleado.idEmpleado FROM Usuario u 
            WHERE u.empleado IS NOT NULL AND u.idUsuario <> :usuarioId
        )
        ORDER BY e.persona.nombres ASC
    """)
    List<EmpleadoSelectDto> listarParaEditar(@Param("usuarioId") Long usuarioId);
}
