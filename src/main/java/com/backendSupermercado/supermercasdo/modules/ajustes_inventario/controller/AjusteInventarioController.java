package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioListadoDto;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioResponse;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CancelarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CrearAjusteInventarioRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.RechazarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service.AjusteInventarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventario/ajustes")
public class AjusteInventarioController {

    private final AjusteInventarioService service;

    @PostMapping
    public ResponseEntity<AjusteInventarioResponse> crear(@Valid @RequestBody CrearAjusteInventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public AjusteInventarioResponse actualizar(@PathVariable Long id,
            @Valid @RequestBody CrearAjusteInventarioRequest request) {
        return service.actualizarBorrador(id, request);
    }

    @GetMapping("/{id}")
    public AjusteInventarioResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(@PathVariable Long id) {
        byte[] pdf = service.exportarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ajuste_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    @GetMapping
    public Page<AjusteInventarioListadoDto> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) TipoAjusteInventario tipo,
            @RequestParam(required = false) MotivoAjusteInventario motivo,
            @RequestParam(required = false) EstadoAjusteInventario estado,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        if (page < 0) throw new IllegalArgumentException("La pagina no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamano debe estar entre 1 y 100");
        String campo = Map.of(
                "fechaCreacion", "fechaCreacion",
                "codigo", "codigo",
                "tipo", "tipo",
                "motivo", "motivo",
                "estado", "estado").get(sortBy);
        if (campo == null) throw new IllegalArgumentException("Campo de ordenamiento no permitido");
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, campo));
        return service.listar(buscar, tipo, motivo, estado, fechaDesde, fechaHasta, pageable);
    }

    @PatchMapping("/{id}/enviar")
    public AjusteInventarioResponse enviar(@PathVariable Long id) { return service.enviar(id); }

    @PatchMapping("/{id}/aprobar")
    public AjusteInventarioResponse aprobar(@PathVariable Long id) { return service.aprobar(id); }

    @PatchMapping("/{id}/rechazar")
    public AjusteInventarioResponse rechazar(@PathVariable Long id,
            @Valid @RequestBody RechazarAjusteRequest request) {
        return service.rechazar(id, request);
    }

    @PatchMapping("/{id}/aplicar")
    public AjusteInventarioResponse aplicar(@PathVariable Long id) { return service.aplicar(id); }

    @PatchMapping("/{id}/cancelar")
    public AjusteInventarioResponse cancelar(@PathVariable Long id,
            @Valid @RequestBody CancelarAjusteRequest request) {
        return service.cancelar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBorrador(@PathVariable Long id) {
        service.eliminarBorrador(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/catalogos")
    public Map<String, Object> catalogos() {
        return Map.of("tipos", Arrays.asList(TipoAjusteInventario.values()),
                "motivos", Arrays.asList(MotivoAjusteInventario.values()),
                "estados", Arrays.asList(EstadoAjusteInventario.values()));
    }
}
