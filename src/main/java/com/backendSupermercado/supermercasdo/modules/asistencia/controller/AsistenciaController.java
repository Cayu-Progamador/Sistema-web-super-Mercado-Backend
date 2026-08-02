package com.backendSupermercado.supermercasdo.modules.asistencia.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResumenDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.AsistenciaService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping("/marcar")
    public ResponseEntity<AsistenciaResponseDto> marcar(
            @RequestBody AsistenciaRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();

        AsistenciaResponseDto result;
        if ("SALIDA".equalsIgnoreCase(dto.getTipo())) {
            result = asistenciaService.marcarSalida(username);
        } else {
            result = asistenciaService.marcarEntrada(username);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/hoy")
    public ResponseEntity<AsistenciaResponseDto> obtenerAsistenciaHoy(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AsistenciaResponseDto dto = asistenciaService.obtenerAsistenciaHoy(userDetails.getUsername());
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/mis-asistencias")
    public ResponseEntity<List<AsistenciaResponseDto>> misAsistencias(
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(asistenciaService.listarMisAsistencias(
                userDetails.getUsername(), fechaDesde, fechaHasta));
    }

    @GetMapping("/mi-resumen")
    public ResponseEntity<AsistenciaResumenDto> miResumen(
            @RequestParam int anio,
            @RequestParam int mes,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(asistenciaService.obtenerMiResumen(
                userDetails.getUsername(), anio, mes));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<AsistenciaResponseDto>> listarAsistencias(
            AsistenciaFiltrosDto filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fecha") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(asistenciaService.listarAsistencias(filtros, pageable));
    }

    @PostMapping("/justificar")
    public ResponseEntity<AsistenciaResponseDto> justificarMiAsistencia(
            @RequestBody AsistenciaJustificarRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AsistenciaResponseDto result = asistenciaService.justificarMiAsistencia(
                userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/mis-ausencias")
    public ResponseEntity<List<Map<String, Object>>> misAusencias(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(asistenciaService.listarAusenciasRecientes(userDetails.getUsername()));
    }

    @PutMapping("/{id}/justificar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AsistenciaResponseDto> justificar(
            @PathVariable Long id,
            @RequestBody AsistenciaJustificarRequestDto dto) {
        AsistenciaResponseDto result = asistenciaService.justificarAsistencia(id, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tiene-acceso")
    public ResponseEntity<Map<String, Object>> tieneAcceso(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean acceso = asistenciaService.tieneAccesoAsistencia(userDetails.getUsername());
        boolean esAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return ResponseEntity.ok(Map.of("tieneAcceso", acceso, "esAdmin", esAdmin));
    }

    @GetMapping("/descargar-reporte")
    public ResponseEntity<byte[]> descargarReporte(
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        byte[] pdf = asistenciaService.descargarReporteSemanal(
                userDetails.getUsername(), fechaDesde, fechaHasta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-asistencia-semanal.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @PostMapping("/cierre-diario")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> ejecutarCierreDiario() {
        long registros = asistenciaService.ejecutarCierreDiario();
        return ResponseEntity.ok(registros);
    }
}
