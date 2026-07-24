package com.backendSupermercado.supermercasdo.modules.asistencia.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;

public interface AsistenciaAdminService {

    Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, Pageable pageable);

    Map<String, Object> obtenerResumenHoy();

    Map<String, Object> obtenerDetalleEmpleado(Long idContrato, int anio, int mes);

    AsistenciaResponseDto justificarAsistencia(Long idAsistencia, AsistenciaJustificarRequestDto dto);

    List<Map<String, Object>> obtenerCalendarioMensual(Long idContrato, int anio, int mes);
}
