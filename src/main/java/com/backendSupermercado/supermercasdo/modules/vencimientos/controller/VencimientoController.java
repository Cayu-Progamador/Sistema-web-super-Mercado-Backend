package com.backendSupermercado.supermercasdo.modules.vencimientos.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoFiltroDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResponseDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResumenDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.service.VencimientoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/vencimientos")
@RequiredArgsConstructor
public class VencimientoController {

    private static final int TAMANO_MAXIMO_PAGINA = 100;

    private final VencimientoService vencimientoService;

    @GetMapping
    public ResponseEntity<Page<VencimientoResponseDto>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        validarPaginacion(page, size);

        VencimientoFiltroDto filtros = new VencimientoFiltroDto(
                buscar,
                estado,
                fechaDesde,
                fechaHasta);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "lote.fechaVencimiento")
                        .and(Sort.by(Sort.Direction.ASC, "producto.nombre")));

        return ResponseEntity.ok(vencimientoService.listar(filtros, pageable));
    }

    @GetMapping("/resumen")
    public ResponseEntity<VencimientoResumenDto> obtenerResumen() {
        return ResponseEntity.ok(vencimientoService.obtenerResumen());
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        VencimientoFiltroDto filtros = new VencimientoFiltroDto(
                buscar, estado, fechaDesde, fechaHasta);
        byte[] pdf = vencimientoService.exportarPdf(filtros);
        String nombre = "vencimientos_" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    private void validarPaginacion(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (size < 1 || size > TAMANO_MAXIMO_PAGINA) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y 100");
        }
    }
}
