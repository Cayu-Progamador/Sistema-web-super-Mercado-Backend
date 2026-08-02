package com.backendSupermercado.supermercasdo.modules.permiso_personal.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.AdminActionDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.HistorialSolicitudDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoRequestDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoResponseDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.service.SolicitudPermisoService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes-permiso")
@RequiredArgsConstructor
public class SolicitudPermisoController {

    private final SolicitudPermisoService solicitudPermisoService;

    @PostMapping
    public ResponseEntity<SolicitudPermisoResponseDto> crear(
            @RequestBody SolicitudPermisoRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudPermisoService.crear(userDetails.getId(), dto));
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudPermisoResponseDto>> misSolicitudes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(solicitudPermisoService.listarMisSolicitudes(userDetails.getId()));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<SolicitudPermisoResponseDto>> listarTodas() {
        return ResponseEntity.ok(solicitudPermisoService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudPermisoResponseDto>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(solicitudPermisoService.listarPorEstado(estado));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SolicitudPermisoResponseDto> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) AdminActionDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String motivo = dto != null ? dto.getComentario() : null;
        return ResponseEntity.ok(solicitudPermisoService.cancelar(id, userDetails.getId(), motivo));
    }

    @PutMapping("/{id}/revisar")
    public ResponseEntity<SolicitudPermisoResponseDto> revisar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(solicitudPermisoService.revisar(id, userDetails.getId()));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudPermisoResponseDto> aprobar(
            @PathVariable Long id,
            @RequestBody(required = false) AdminActionDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String comentario = dto != null ? dto.getComentario() : null;
        return ResponseEntity.ok(solicitudPermisoService.aprobar(id, userDetails.getId(), comentario));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudPermisoResponseDto> rechazar(
            @PathVariable Long id,
            @RequestBody(required = false) AdminActionDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String comentario = dto != null ? dto.getComentario() : null;
        return ResponseEntity.ok(solicitudPermisoService.rechazar(id, userDetails.getId(), comentario));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialSolicitudDto>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudPermisoService.obtenerHistorial(id));
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) String estado) {
        byte[] excel = solicitudPermisoService.exportarExcel(estado);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "solicitudes_permiso.xlsx");
        headers.setContentLength(excel.length);
        return ResponseEntity.ok().headers(headers).body(excel);
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) String estado,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = solicitudPermisoService.exportarPdf(estado, username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "solicitudes_permiso.pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
