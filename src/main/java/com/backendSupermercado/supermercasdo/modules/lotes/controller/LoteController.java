package com.backendSupermercado.supermercasdo.modules.lotes.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.*;
import com.backendSupermercado.supermercasdo.modules.lotes.service.LoteService;

@RestController
@RequestMapping("/api/inventario/lotes")
@RequiredArgsConstructor
public class LoteController {
    private final LoteService service;

    @GetMapping("/estados")
    public ResponseEntity<List<String>> estados() {
        return ResponseEntity.ok(List.of("ACTIVO", "POR_VENCER", "VENCIDO", "AGOTADO", "BLOQUEADO"));
    }

    @PutMapping("/{idLote}")
    public ResponseEntity<LoteResponseDto> actualizar(@PathVariable Long idLote,
            @Valid @RequestBody LoteUpdateRequestDto dto) {
        return ResponseEntity.ok(service.actualizar(idLote, dto));
    }

    @GetMapping
    public ResponseEntity<Page<LoteResponseDto>> listar(@RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idProducto, @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate fechaDesde, @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        if (page < 0 || size < 1 || size > 100)
            throw new IllegalArgumentException("Paginación inválida");
        return ResponseEntity.ok(service.listar(buscar, idProducto, estado, fechaDesde, fechaHasta,
                PageRequest.of(page, size, Sort.by("fechaVencimiento").ascending())));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta) {
        byte[] pdf = service.exportarPdf(buscar, idProducto, estado, fechaDesde, fechaHasta);
        String nombre = "lotes_" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
