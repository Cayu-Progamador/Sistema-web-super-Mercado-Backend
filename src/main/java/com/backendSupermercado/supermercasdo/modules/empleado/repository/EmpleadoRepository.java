package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoDisponibleDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long>, JpaSpecificationExecutor<Empleado> {

    long countByEstado(Boolean estado);
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

    @Query("""
        SELECT new com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoDisponibleDto(
            e.idEmpleado,
            CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
            p.ci,
            (SELECT c.correo FROM Contacto c WHERE c.persona = p),
            (SELECT c.telefono FROM Contacto c WHERE c.persona = p)
        )
        FROM Empleado e
        JOIN e.persona p
        WHERE e.estado = true
        AND e.idEmpleado NOT IN (
            SELECT ct.empleado.idEmpleado FROM Contrato ct
        )
        AND (:busqueda IS NULL
             OR LOWER(CAST(p.nombres AS text)) LIKE LOWER(CONCAT('%', :busqueda, '%'))
             OR LOWER(CAST(p.apellidoPaterno AS text)) LIKE LOWER(CONCAT('%', :busqueda, '%'))
             OR LOWER(CAST(p.apellidoMaterno AS text)) LIKE LOWER(CONCAT('%', :busqueda, '%'))
             OR LOWER(p.ci) LIKE LOWER(CONCAT('%', :busqueda, '%')))
        ORDER BY p.nombres ASC
    """)
    Page<EmpleadoDisponibleDto> listarDisponiblesParaContrato(@Param("busqueda") String busqueda, Pageable pageable);
}
