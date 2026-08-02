package com.backendSupermercado.supermercasdo.modules.asistencia.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaCrearRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaEditDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDetalleDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.AsistenciaAdminService;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.impl.AsistenciaExportService;
import com.backendSupermercado.supermercasdo.modules.turno.dto.TurnoDto;
import com.backendSupermercado.supermercasdo.modules.turno.service.TurnoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias/admin")
@RequiredArgsConstructor
public class AsistenciaAdminController {

    private final AsistenciaAdminService adminService;
    private final TurnoService turnoService;
    private final AsistenciaExportService exportService;

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
                Map.of("label", "Permiso", "value", "PERMISO"),
                Map.of("label", "Pendiente salida", "value", "PRESENTE")
        );
        return ResponseEntity.ok(Map.of("turnos", turnos, "estados", estados));
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<Resource> exportarPDF(
            AsistenciaFiltrosDto filtros,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idTurno,
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        byte[] pdfBytes = exportService.exportarPDF(filtros, busqueda, idTurno, sortBy, sortDir);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asistencia.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PostMapping("/justificar-ausente/{idContrato}")
    public ResponseEntity<AsistenciaResponseDto> justificarAusente(
            @PathVariable Long idContrato,
            @RequestBody AsistenciaJustificarRequestDto dto) {
        return ResponseEntity.ok(adminService.justificarAusente(idContrato, dto));
    }

    @PostMapping("/crear")
    public ResponseEntity<AsistenciaResponseDto> crearAsistencia(
            @RequestBody AsistenciaCrearRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearAsistencia(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaResponseDto> editarAsistencia(
            @PathVariable Long id,
            @RequestBody AsistenciaEditDto dto) {
        return ResponseEntity.ok(adminService.editarAsistencia(id, dto));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> exportarPdfIndividual(@PathVariable Long id) {
        byte[] pdfBytes = adminService.exportarPdfIndividual(id);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asistencia-individual.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<Resource> exportarExcel(
            AsistenciaFiltrosDto filtros,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long idTurno,
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        byte[] excelBytes = exportService.exportarExcel(filtros, busqueda, idTurno, sortBy, sortDir);
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=asistencia.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping("/ausentes")
    public ResponseEntity<List<AusenteDto>> listarAusentes(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        if (fecha != null) {
            return ResponseEntity.ok(adminService.listarAusentesDelDia(fecha));
        }
        return ResponseEntity.ok(adminService.listarAusentes(mes, anio));
    }

    @GetMapping("/ausentes/detalle")
    public ResponseEntity<List<AusenteDetalleDto>> listarAusentesDetalle(
            @RequestParam int mes,
            @RequestParam int anio) {
        return ResponseEntity.ok(adminService.listarAusentesDetalle(mes, anio));
    }
}
