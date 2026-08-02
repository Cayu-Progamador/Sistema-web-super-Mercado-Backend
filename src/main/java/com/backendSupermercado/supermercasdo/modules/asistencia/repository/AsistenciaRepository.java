package com.backendSupermercado.supermercasdo.modules.asistencia.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.entity.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByContratoIdAndFecha(Long idContrato, LocalDate fecha);

    List<Asistencia> findByContratoIdAndFechaBetweenAndEstado(
            Long idContrato, LocalDate fechaDesde, LocalDate fechaHasta, String estado);

    @Query("""
                SELECT new com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto(
                    a.id,
                    c.id,
                    CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
                    a.fecha,
                    a.horaEntrada,
                    a.horaSalida,
                    a.estado,
                    a.minutosRetraso
                )
                FROM Asistencia a
                JOIN a.contrato c
                JOIN c.empleado e
                JOIN e.persona p
                WHERE (:idContrato IS NULL OR c.id = :idContrato)
                AND (:fechaDesde IS NULL OR a.fecha >= :fechaDesde)
                AND (:fechaHasta IS NULL OR a.fecha <= :fechaHasta)
                AND (:estado IS NULL OR a.estado = :estado)
                ORDER BY a.fecha DESC, a.horaEntrada DESC
            """)
    Page<AsistenciaResponseDto> buscarAsistencias(
            @Param("idContrato") Long idContrato,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("estado") String estado,
            Pageable pageable);

    @Query("""
                SELECT new com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto(
                    a.id,
                    c.id,
                    CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
                    a.fecha,
                    a.horaEntrada,
                    a.horaSalida,
                    a.estado,
                    a.minutosRetraso
                )
                FROM Asistencia a
                JOIN a.contrato c
                JOIN c.empleado e
                JOIN e.persona p
                WHERE c.id = :idContrato
                AND a.fecha >= COALESCE(:fechaDesde, a.fecha)
                AND a.fecha <= COALESCE(:fechaHasta, a.fecha)
                ORDER BY a.fecha DESC
            """)
    List<AsistenciaResponseDto> findMisAsistencias(
            @Param("idContrato") Long idContrato,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta);

    @Query("""
                SELECT COUNT(a)
                FROM Asistencia a
                WHERE a.contrato.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                AND a.horaEntrada IS NOT NULL
            """)
    long countAsistenciasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                SELECT COUNT(a)
                FROM Asistencia a
                WHERE a.contrato.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                AND a.estado = 'TARDANZA'
            """)
    long countTardanzasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                SELECT COUNT(a)
                FROM Asistencia a
                WHERE a.contrato.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                AND (a.estado = 'FALTA' OR (a.horaEntrada IS NULL AND a.estado IS NULL))
            """)
    long countFaltasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                SELECT COUNT(a)
                FROM Asistencia a
                WHERE a.contrato.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                AND a.estado = 'JUSTIFICADO'
            """)
    long countJustificadosDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                SELECT COUNT(a)
                FROM Asistencia a
                WHERE a.contrato.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                AND a.estado = 'PERMISO'
            """)
    long countPermisosDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                    SELECT new com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto(
                        a.id,
                        c.id,
                        CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
                        a.fecha,
                        a.horaEntrada,
                        a.horaSalida,
                        a.estado,
                        a.minutosRetraso,
                        a.horasTrabajadas,
                        ca.nombre
                    )
                    FROM Asistencia a
                    JOIN a.contrato c
                    JOIN c.empleado e
                    JOIN e.persona p
                    JOIN c.cargo ca
                    WHERE c.controlaAsistencia = true
                    AND a.fecha >= COALESCE(:fechaDesde, a.fecha)
                    AND a.fecha <= COALESCE(:fechaHasta, a.fecha)
                    AND a.estado = COALESCE(:estado, a.estado)
                    AND c.id = COALESCE(:idContrato, c.id)
                    AND (:idTurno IS NULL OR EXISTS (
                        SELECT 1 FROM ContratoTurno ct WHERE ct.contrato.id = c.id AND ct.turno.id = :idTurno
                    ))
            AND (:busqueda IS NULL OR (
                FUNCTION('lower', p.nombres) LIKE :busqueda
                OR FUNCTION('lower', p.apellidoPaterno) LIKE :busqueda
                OR FUNCTION('lower', p.apellidoMaterno) LIKE :busqueda
            ))
                ORDER BY a.fecha DESC, a.horaEntrada DESC
                """)
    Page<AsistenciaResponseDto> buscarAsistenciasAdmin(
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("estado") String estado,
            @Param("idContrato") Long idContrato,
            @Param("idTurno") Long idTurno,
            @Param("busqueda") String busqueda,
            Pageable pageable);

    @Query("""
                SELECT new com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto(
                    a.id,
                    c.id,
                    CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
                    :fecha,
                    a.horaEntrada,
                    a.horaSalida,
                    a.estado,
                    a.minutosRetraso,
                    a.horasTrabajadas,
                    ca.nombre
                )
                FROM Contrato c
                JOIN c.empleado e
                JOIN e.persona p
                JOIN c.cargo ca
                LEFT JOIN Asistencia a ON a.contrato = c AND a.fecha = :fecha
                WHERE c.estado = 'ACTIVO'
                AND c.controlaAsistencia = true
                AND (:idContrato IS NULL OR c.id = :idContrato)
                AND (:estado IS NULL OR a.estado = :estado)
                AND (:idTurno IS NULL OR EXISTS (
                    SELECT 1 FROM ContratoTurno ct WHERE ct.contrato.id = c.id AND ct.turno.id = :idTurno
                ))
                AND (:busqueda IS NULL OR (
                    FUNCTION('lower', p.nombres) LIKE :busqueda
                    OR FUNCTION('lower', p.apellidoPaterno) LIKE :busqueda
                    OR FUNCTION('lower', p.apellidoMaterno) LIKE :busqueda
                ))
                AND (
                    a.fecha IS NOT NULL
                    OR EXISTS (
                        SELECT 1 FROM ContratoTurno ct
                        WHERE ct.contrato.id = c.id
                        AND (
                            (:esLunes = true AND ct.lunes = true)
                            OR (:esMartes = true AND ct.martes = true)
                            OR (:esMiercoles = true AND ct.miercoles = true)
                            OR (:esJueves = true AND ct.jueves = true)
                            OR (:esViernes = true AND ct.viernes = true)
                            OR (:esSabado = true AND ct.sabado = true)
                            OR (:esDomingo = true AND ct.domingo = true)
                        )
                    )
                )
                ORDER BY p.nombres ASC
            """)
    Page<AsistenciaResponseDto> buscarAsistenciasDelDia(
            @Param("fecha") LocalDate fecha,
            @Param("estado") String estado,
            @Param("idContrato") Long idContrato,
            @Param("idTurno") Long idTurno,
            @Param("busqueda") String busqueda,
            @Param("esLunes") boolean esLunes,
            @Param("esMartes") boolean esMartes,
            @Param("esMiercoles") boolean esMiercoles,
            @Param("esJueves") boolean esJueves,
            @Param("esViernes") boolean esViernes,
            @Param("esSabado") boolean esSabado,
            @Param("esDomingo") boolean esDomingo,
            Pageable pageable);

    @Query("""
                SELECT a FROM Asistencia a
                JOIN FETCH a.contrato c
                JOIN FETCH c.empleado e
                JOIN FETCH e.persona p
                WHERE c.controlaAsistencia = true
                AND a.fecha = :fecha
            """)
    List<Asistencia> findAllByFechaWithContrato(@Param("fecha") LocalDate fecha);

    @Query("""
                SELECT a FROM Asistencia a
                JOIN FETCH a.contrato c
                LEFT JOIN FETCH c.contratoTurnos ct
                LEFT JOIN FETCH ct.turno
                WHERE c.id = :idContrato
                AND a.fecha BETWEEN :inicioMes AND :finMes
                ORDER BY a.fecha ASC
            """)
    List<Asistencia> findAsistenciasDelMesWithTurno(
            @Param("idContrato") Long idContrato,
            @Param("inicioMes") LocalDate inicioMes,
            @Param("finMes") LocalDate finMes);

    @Query("""
                SELECT new com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDto(
                    e.idEmpleado,
                    CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', p.apellidoMaterno),
                    ca.nombre,
                    MIN(ct.telefono)
                )
                FROM Contrato c
                JOIN c.empleado e
                JOIN e.persona p
                JOIN c.cargo ca
                LEFT JOIN p.contactos ct
                WHERE c.estado = 'ACTIVO'
                AND c.controlaAsistencia = true
                AND NOT EXISTS (
                    SELECT a FROM Asistencia a
                    WHERE a.contrato = c
                    AND a.fecha BETWEEN :inicio AND :fin
                )
                GROUP BY e.idEmpleado, p.nombres, p.apellidoPaterno, p.apellidoMaterno, ca.nombre
            """)
    List<AusenteDto> findAusentesDelMes(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
