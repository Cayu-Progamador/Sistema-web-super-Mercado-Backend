package com.backendSupermercado.supermercasdo.modules.asistencia.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.AsistenciaAdminService;
import com.backendSupermercado.supermercasdo.modules.turno.dto.TurnoDto;
import com.backendSupermercado.supermercasdo.modules.turno.service.TurnoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias/admin")
@RequiredArgsConstructor
public class AsistenciaAdminController {

    private final AsistenciaAdminService adminService;
    private final TurnoService turnoService;

    @GetMapping("/listar")
    public ResponseEntity<Page<AsistenciaResponseDto>> listarAsistencias(
            AsistenciaFiltrosDto filtros,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idTurno,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(adminService.listarAsistencias(filtros, busqueda, idTurno, pageable));
    }

    @GetMapping("/resumen-hoy")
    public ResponseEntity<Map<String, Object>> resumenHoy() {
        return ResponseEntity.ok(adminService.obtenerResumenHoy());
    }

    @GetMapping("/empleado/{idContrato}/detalle")
    public ResponseEntity<Map<String, Object>> detalleEmpleado(
            @PathVariable Long idContrato,
            @RequestParam int anio,
            @RequestParam int mes) {
        return ResponseEntity.ok(adminService.obtenerDetalleEmpleado(idContrato, anio, mes));
    }

    @GetMapping("/empleado/{idContrato}/calendario")
    public ResponseEntity<List<Map<String, Object>>> calendarioMensual(
            @PathVariable Long idContrato,
            @RequestParam int anio,
            @RequestParam int mes) {
        return ResponseEntity.ok(adminService.obtenerCalendarioMensual(idContrato, anio, mes));
    }

    @GetMapping("/filtros")
    public ResponseEntity<Map<String, Object>> obtenerFiltros() {
        List<TurnoDto> turnos = turnoService.listarTodos(null, true);
        List<Map<String, Object>> estados = Arrays.asList(
                Map.of("label", "A tiempo", "value", "COMPLETO"),
                Map.of("label", "Tardanza", "value", "TARDANZA"),
                Map.of("label", "Falta", "value", "FALTA"),
                Map.of("label", "Justificado", "value", "JUSTIFICADO"),
                Map.of("label", "Pendiente salida", "value", "PRESENTE")
        );
        return ResponseEntity.ok(Map.of("turnos", turnos, "estados", estados));
    }
}
