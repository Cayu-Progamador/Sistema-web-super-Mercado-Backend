package com.backendSupermercado.supermercasdo.modules.asistencia.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaCrearRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDetalleDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaEditDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;

public interface AsistenciaAdminService {

    Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, Pageable pageable);

    Map<String, Object> obtenerResumenHoy();

    Map<String, Object> obtenerDetalleEmpleado(Long idContrato, int anio, int mes);

    AsistenciaResponseDto justificarAsistencia(Long idAsistencia, AsistenciaJustificarRequestDto dto);

    AsistenciaResponseDto justificarAusente(Long idContrato, AsistenciaJustificarRequestDto dto);

    AsistenciaResponseDto crearAsistencia(AsistenciaCrearRequestDto dto);

    AsistenciaResponseDto editarAsistencia(Long idAsistencia, AsistenciaEditDto dto);

    byte[] exportarPdfIndividual(Long idAsistencia);

    List<Map<String, Object>> obtenerCalendarioMensual(Long idContrato, int anio, int mes);

    List<AusenteDto> listarAusentes(int mes, int anio);

    List<AusenteDto> listarAusentesDelDia(LocalDate fecha);

    List<AusenteDetalleDto> listarAusentesDetalle(int mes, int anio);
}
