package com.backendSupermercado.supermercasdo.modules.kardex.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexFiltroDto;
import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexResponseDto;
import com.backendSupermercado.supermercasdo.modules.kardex.service.KardexService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/kardex")
@RequiredArgsConstructor
public class KardexController {

    private static final int TAMANO_MAXIMO_PAGINA = 100;

    private final KardexService kardexService;

    @GetMapping
    public ResponseEntity<Page<KardexResponseDto>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idLote,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        validarPaginacion(page, size);

        KardexFiltroDto filtros = new KardexFiltroDto(
                buscar,
                idProducto,
                idLote,
                fechaDesde,
                fechaHasta);

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "movimiento.fechaMovimiento")
                        .and(Sort.by(Sort.Direction.DESC, "idDetalleMovimiento")));

        return ResponseEntity.ok(kardexService.listar(filtros, pageable));
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Long idLote,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {

        KardexFiltroDto filtros = new KardexFiltroDto(
                buscar,
                idProducto,
                idLote,
                fechaDesde,
                fechaHasta);
        byte[] pdf = kardexService.exportarPdf(filtros);
        String nombreArchivo = "kardex_" + LocalDate.now() + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombreArchivo + "\"")
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
