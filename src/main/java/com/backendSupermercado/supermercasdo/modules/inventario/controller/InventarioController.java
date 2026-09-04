package com.backendSupermercado.supermercasdo.modules.inventario.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.ExistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.inventario.service.InventarioService;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService service;
    @GetMapping("/resumen")
    public ResponseEntity<com.backendSupermercado.supermercasdo.modules.inventario.dto.InventarioResumenDto> resumen() { return ResponseEntity.ok(service.obtenerResumen()); }
    @GetMapping("/existencias/{id}")
    public ResponseEntity<ExistenciaResponseDto> detalle(@PathVariable Long id) { return ResponseEntity.ok(service.obtenerDetalle(id)); }

    @GetMapping("/existencias/estados")
    public ResponseEntity<List<String>> estados() {
        return ResponseEntity.ok(List.of("DISPONIBLE", "STOCK_BAJO", "SIN_STOCK", "SOBRESTOCK"));
    }

    @GetMapping("/existencias")
    public ResponseEntity<Page<ExistenciaResponseDto>> listar(@RequestParam(required=false) String buscar,
            @RequestParam(required=false) String almacen, @RequestParam(required=false) String ubicacion,
            @RequestParam(required=false) String categoria, @RequestParam(required=false) String estado, @RequestParam(required=false) String lote,
            @RequestParam(required=false) LocalDate vencimientoHasta,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="8") int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page debe ser >= 0 y size entre 1 y 100");
        return ResponseEntity.ok(service.listarExistencias(buscar,almacen,ubicacion,categoria,estado,lote,vencimientoHasta, PageRequest.of(page,size,Sort.by(Sort.Direction.ASC,"producto.nombre"))));
    }

    @GetMapping("/existencias/pdf")
    public ResponseEntity<byte[]> exportarExistenciasPdf(
            @RequestParam(required=false) String buscar,
            @RequestParam(required=false) String almacen,
            @RequestParam(required=false) String ubicacion,
            @RequestParam(required=false) String categoria,
            @RequestParam(required=false) String estado,
            @RequestParam(required=false) String lote,
            @RequestParam(required=false) LocalDate vencimientoHasta) {
        byte[] pdf = service.exportarExistenciasPdf(
                buscar, almacen, ubicacion, categoria, estado, lote, vencimientoHasta);
        return respuestaPdf(pdf, "existencias_" + LocalDate.now() + ".pdf");
    }

    private ResponseEntity<byte[]> respuestaPdf(byte[] pdf, String nombreArchivo) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
