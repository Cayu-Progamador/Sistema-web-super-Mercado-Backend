package com.backendSupermercado.supermercasdo.modules.devoluciones.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.CrearDevolucionRequestDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.DevolucionProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.RecepcionDisponibleDevolucionDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.service.DevolucionProveedorService;
import com.backendSupermercado.supermercasdo.modules.devoluciones.service.DevolucionPdfService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class DevolucionProveedorController {

    private final DevolucionProveedorService devolucionService;
    private final DevolucionPdfService devolucionPdfService;

    @PostMapping
    public ResponseEntity<DevolucionProveedorResponseDto> crearBorrador(
            @Valid @RequestBody CrearDevolucionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(devolucionService.crearBorrador(idUsuario, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DevolucionProveedorResponseDto> actualizarBorrador(
            @PathVariable Long id,
            @Valid @RequestBody CrearDevolucionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(devolucionService.actualizarBorrador(idUsuario, id, request));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<DevolucionProveedorResponseDto> confirmar(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(devolucionService.confirmar(idUsuario, id));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<DevolucionProveedorResponseDto> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.enviar(id));
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<DevolucionProveedorResponseDto> completar(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.completar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<DevolucionProveedorResponseDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.cancelar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DevolucionProveedorResponseDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(devolucionService.obtener(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        DevolucionProveedorResponseDto devolucion = devolucionService.obtener(id);
        byte[] pdf = devolucionPdfService.generar(devolucion);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Devolucion_" + devolucion.getNumero() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping
    public ResponseEntity<List<DevolucionProveedorResponseDto>> listar() {
        return ResponseEntity.ok(devolucionService.listar());
    }

    @GetMapping("/estados")
    public ResponseEntity<List<String>> listarEstados() {
        return ResponseEntity.ok(devolucionService.listarEstados());
    }

    @GetMapping("/recepciones-disponibles")
    public ResponseEntity<List<RecepcionDisponibleDevolucionDto>> listarRecepcionesDisponibles() {
        return ResponseEntity.ok(devolucionService.listarRecepcionesDisponibles());
    }
}
