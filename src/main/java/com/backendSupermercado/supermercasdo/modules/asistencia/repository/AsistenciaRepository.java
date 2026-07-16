package com.backendSupermercado.supermercasdo.modules.asistencia.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.entity.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByContratoIdAndFecha(Long idContrato, LocalDate fecha);

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
        Pageable pageable
    );

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
        AND (:fechaDesde IS NULL OR a.fecha >= :fechaDesde)
        AND (:fechaHasta IS NULL OR a.fecha <= :fechaHasta)
        ORDER BY a.fecha DESC
    """)
    List<AsistenciaResponseDto> findMisAsistencias(
        @Param("idContrato") Long idContrato,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    @Query("""
        SELECT COUNT(a)
        FROM Asistencia a
        WHERE a.contrato.id = :idContrato
        AND a.fecha BETWEEN :inicioMes AND :finMes
        AND a.horaEntrada IS NOT NULL
    """)
    long countAsistenciasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes, @Param("finMes") LocalDate finMes);

    @Query("""
        SELECT COUNT(a)
        FROM Asistencia a
        WHERE a.contrato.id = :idContrato
        AND a.fecha BETWEEN :inicioMes AND :finMes
        AND a.estado = 'TARDANZA'
    """)
    long countTardanzasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes, @Param("finMes") LocalDate finMes);

    @Query("""
        SELECT COUNT(a)
        FROM Asistencia a
        WHERE a.contrato.id = :idContrato
        AND a.fecha BETWEEN :inicioMes AND :finMes
        AND (a.estado = 'FALTA' OR (a.horaEntrada IS NULL AND a.estado IS NULL))
    """)
    long countFaltasDelMes(@Param("idContrato") Long idContrato, @Param("inicioMes") LocalDate inicioMes, @Param("finMes") LocalDate finMes);
}
