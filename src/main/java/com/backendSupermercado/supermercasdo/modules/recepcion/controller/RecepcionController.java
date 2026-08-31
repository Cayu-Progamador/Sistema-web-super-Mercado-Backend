package com.backendSupermercado.supermercasdo.modules.recepcion.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import com.backendSupermercado.supermercasdo.modules.recepcion.dto.CrearRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DevolucionRecepcionDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DistribuirRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.ProductoFaltanteDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionListadoDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.service.RecepcionService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recepciones")
@RequiredArgsConstructor
public class RecepcionController {

    private final RecepcionService recepcionService;

    @PostMapping
    public ResponseEntity<RecepcionListadoDto> crear(
            @Valid @RequestBody CrearRecepcionRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionService.crear(idUsuario, dto));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<RecepcionListadoDto> anular(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(recepcionService.anular(idUsuario, id));
    }

    @PutMapping("/{id}/distribuir")
    public ResponseEntity<RecepcionListadoDto> distribuir(
            @PathVariable Long id,
            @Valid @RequestBody DistribuirRecepcionRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(recepcionService.distribuir(idUsuario, id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<RecepcionListadoDto>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaRecepcion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(recepcionService.listar(estado, search, idProveedor,
                parseFecha(fechaDesde), parseFecha(fechaHasta), pageable));
    }

    @GetMapping("/estados")
    public ResponseEntity<List<String>> listarEstados() {
        return ResponseEntity.ok(recepcionService.listarEstados());
    }

    @GetMapping("/faltantes")
    public ResponseEntity<List<ProductoFaltanteDto>> listarFaltantes() {
        return ResponseEntity.ok(recepcionService.listarFaltantes());
    }

    @GetMapping("/devoluciones")
    public ResponseEntity<List<DevolucionRecepcionDto>> listarDevoluciones() {
        return ResponseEntity.ok(recepcionService.listarDevoluciones());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = recepcionService.exportarPdf(id, username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Recepcion_" + id + ".pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Long>> kpi() {
        Map<String, Long> kpi = new HashMap<>();
        kpi.put("total", recepcionService.contarPorEstado(null));
        kpi.put("enRevision", recepcionService.contarPorEstado("EN_REVISION"));
        kpi.put("completas", recepcionService.contarPorEstado("COMPLETADA"));
        kpi.put("parciales", recepcionService.contarPorEstado("PARCIAL"));
        kpi.put("rechazadas", recepcionService.contarPorEstado("RECHAZADA"));
        kpi.put("anuladas", recepcionService.contarPorEstado("ANULADA"));
        return ResponseEntity.ok(kpi);
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (Exception e) {
            return null;
        }
    }
}