package com.backendSupermercado.supermercasdo.modules.venta.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;
import com.backendSupermercado.supermercasdo.modules.venta.dto.CrearVentaRequest;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResponseDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaCatalogoDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VerificacionVentaDto;
import com.backendSupermercado.supermercasdo.modules.venta.service.VentaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService service;

    @PostMapping
    public ResponseEntity<VentaResponseDto> crear(@Valid @RequestBody CrearVentaRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(userDetails.getId(), request));
    }

    @GetMapping
    public Page<VentaResponseDto> listar(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0) throw new IllegalArgumentException("La pagina no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamano debe estar entre 1 y 100");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        return service.listar(pageable);
    }

    @GetMapping("/catalogo")
    public java.util.List<VentaCatalogoDto> catalogo(@RequestParam(required = false) String buscar) {
        return service.catalogo(buscar);
    }

    @GetMapping("/verificar/{codigo}")
    public VerificacionVentaDto verificar(@PathVariable String codigo) {
        return service.verificar(codigo);
    }

    @GetMapping("/ganancia-diaria")
    public java.math.BigDecimal gananciaDiaria() {
        return service.gananciaDelDia();
    }

    @GetMapping("/resumen")
    public com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResumenDto resumen() {
        return service.resumen();
    }

    @GetMapping("/{id}")
    public VentaResponseDto obtener(@PathVariable Long id) { return service.obtener(id); }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> comprobante(@PathVariable Long id) {
        byte[] pdf = service.generarComprobantePdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante-venta-" + id + ".pdf")
                .body(pdf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> anular(@PathVariable Long id,
            @RequestParam @Size(min = 5, max = 500) String motivo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        service.anular(userDetails.getId(), id, motivo);
        return ResponseEntity.noContent().build();
    }
}
