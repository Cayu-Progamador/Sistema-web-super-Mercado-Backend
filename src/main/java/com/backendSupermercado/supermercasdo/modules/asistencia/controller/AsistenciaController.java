package com.backendSupermercado.supermercasdo.modules.asistencia.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/{id}/justificar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AsistenciaResponseDto> justificar(
            @PathVariable Long id,
            @RequestBody AsistenciaJustificarRequestDto dto) {
        AsistenciaResponseDto result = asistenciaService.justificarAsistencia(id, dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cierre-diario")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> ejecutarCierreDiario() {
        long registros = asistenciaService.ejecutarCierreDiario();
        return ResponseEntity.ok(registros);
    }
}
