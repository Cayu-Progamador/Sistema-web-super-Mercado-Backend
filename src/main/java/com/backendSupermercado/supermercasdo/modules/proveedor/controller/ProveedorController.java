package com.backendSupermercado.supermercasdo.modules.proveedor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorEstadisticasDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorListadoDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorRequestDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.service.ProveedorService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping("/estadisticas")
    public ResponseEntity<ProveedorEstadisticasDto> obtenerEstadisticas() {
        return ResponseEntity.ok(proveedorService.obtenerEstadisticas());
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<ProveedorListadoDto>> listarProveedores(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long ciudad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idProveedor") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        String campoOrden = switch (sortBy) {
            case "tipo" -> "tipoProveedor";
            case "estado" -> "estado";
            case "nombre", "documento", "ciudad", "contacto" -> "idProveedor";
            default -> "idProveedor";
        };
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(campoOrden).descending()
                : Sort.by(campoOrden).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(proveedorService.listarProveedores(busqueda, tipo, estado, ciudad, pageable));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponseDto> crearProveedor(@Valid @RequestBody ProveedorRequestDto request) {
        return ResponseEntity.ok(proveedorService.crearProveedor(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDto> obtenerProveedorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerProveedorPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDto> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequestDto request) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(id, request));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ProveedorResponseDto> activarProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.activarProveedor(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProveedorResponseDto> desactivarProveedor(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.desactivarProveedor(id));
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarProveedoresPDF(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long ciudad,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = proveedorService.exportarProveedoresPDF(busqueda, tipo, estado, ciudad, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_proveedores.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarProveedoresExcel(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long ciudad) {
        byte[] excelBytes = proveedorService.exportarProveedoresExcel(busqueda, tipo, estado, ciudad);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_proveedores.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarProveedorDetallePDF(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = proveedorService.exportarProveedorDetallePDF(id, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ficha_proveedor_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/{id}/excel")
    public ResponseEntity<byte[]> exportarProveedorDetalleExcel(@PathVariable Long id) {
        byte[] excelBytes = proveedorService.exportarProveedorDetalleExcel(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "ficha_proveedor_" + id + ".xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}
