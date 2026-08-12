package com.backendSupermercado.supermercasdo.modules.productos.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoRequestDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.service.ProductoImagenService;
import com.backendSupermercado.supermercasdo.modules.productos.service.ProductoService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoImagenService productoImagenService;

    @GetMapping("/dashboard")
    public ResponseEntity<ProductoDashboardDto> dashboard() {
        return ResponseEntity.ok(productoService.obtenerDashboard());
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody ProductoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearProducto(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDto dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ProductoResponseDto> activarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.activarProducto(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponseDto> desactivarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.desactivarProducto(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<ProductoResponseDto>> listarProductosPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idSubcategoria,
            @RequestParam(required = false) Long idMarca,
            @RequestParam(required = false) Long idUnidadBase,
            @RequestParam(required = false) Boolean tienePresentaciones,
            @RequestParam(required = false) String codigoBarra,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productoService.listarProductosPaginado(
                busqueda, estado, idCategoria, idSubcategoria, idMarca, idUnidadBase,
                tienePresentaciones, codigoBarra, pageable));
    }

    @PostMapping(value = "/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subirImagen(@RequestParam("imagen") MultipartFile file) {
        String nombreArchivo = productoImagenService.subirImagen(file);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Imagen subida correctamente",
                "url", "/uploads/" + nombreArchivo
        ));
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarProductosPDF(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idSubcategoria,
            @RequestParam(required = false) Long idMarca,
            @RequestParam(required = false) Long idUnidadBase,
            @RequestParam(required = false) String codigoBarra,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = productoService.exportarProductosPDF(
                busqueda, estado, idCategoria, idSubcategoria, idMarca, idUnidadBase, codigoBarra, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_productos.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarProductosExcel(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idSubcategoria,
            @RequestParam(required = false) Long idMarca,
            @RequestParam(required = false) Long idUnidadBase,
            @RequestParam(required = false) String codigoBarra) {
        byte[] excelBytes = productoService.exportarProductosExcel(
                busqueda, estado, idCategoria, idSubcategoria, idMarca, idUnidadBase, codigoBarra);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_productos.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}
