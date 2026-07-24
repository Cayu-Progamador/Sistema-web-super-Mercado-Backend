package com.backendSupermercado.supermercasdo.modules.asistencia.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResumenDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;

public interface AsistenciaService {

    AsistenciaResponseDto marcarEntrada(String username);

    AsistenciaResponseDto marcarSalida(String username);

    AsistenciaResponseDto justificarAsistencia(Long idAsistencia, AsistenciaJustificarRequestDto dto);

    AsistenciaResponseDto justificarMiAsistencia(String username, AsistenciaJustificarRequestDto dto);

    long ejecutarCierreDiario();

    AsistenciaResponseDto obtenerAsistenciaHoy(String username);

    Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, Pageable pageable);

    List<AsistenciaResponseDto> listarMisAsistencias(String username, String fechaDesde, String fechaHasta);

    AsistenciaResumenDto obtenerMiResumen(String username, int anio, int mes);

    byte[] descargarReporteSemanal(String username, String fechaDesde, String fechaHasta);

    boolean tieneAccesoAsistencia(String username);
}
