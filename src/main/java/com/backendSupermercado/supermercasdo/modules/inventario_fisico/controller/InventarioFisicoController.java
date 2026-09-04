package com.backendSupermercado.supermercasdo.modules.inventario_fisico.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CrearInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoListadoDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoPaginaDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoResumenDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarConteoFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.FinalizarConteoFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarDiferenciasRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CancelarInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoDetalleDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoEstadoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.service.InventarioFisicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventarios-fisicos")
@RequiredArgsConstructor
public class InventarioFisicoController {

    private final InventarioFisicoService service;

    @PostMapping
    public ResponseEntity<InventarioFisicoResponse> crear(@Valid @RequestBody CrearInventarioFisicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioFisicoResponse> actualizarBorrador(
            @PathVariable Long id,
            @Valid @RequestBody CrearInventarioFisicoRequest request) {
        return ResponseEntity.ok(service.actualizarBorrador(id, request));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<InventarioFisicoResponse> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(service.iniciar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioFisicoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping
    public ResponseEntity<Page<InventarioFisicoListadoDto>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) EstadoInventarioFisico estado,
            @RequestParam(required = false) Long idAlmacen,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        if (page < 0) throw new IllegalArgumentException("La página no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");

        String campoOrden = switch (sortBy) {
            case "codigo", "fechaProgramada", "fechaCreacion", "estado" -> sortBy;
            default -> "fechaCreacion";
        };
        Sort.Direction direccion = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direccion, campoOrden));
        return ResponseEntity.ok(service.listar(
                buscar, estado, idAlmacen, fechaDesde, fechaHasta, pageable));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<ConteoFisicoPaginaDto> listarConteo(
            @PathVariable Long id,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "false") Boolean soloPendientes,
            @RequestParam(defaultValue = "false") Boolean soloDiferencias,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "producto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        if (page < 0) throw new IllegalArgumentException("La página no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");

        String campoOrden = switch (sortBy) {
            case "ubicacion" -> "ubicacion.nombre";
            case "cantidadFisica" -> "cantidadFisica";
            case "diferencia" -> "diferencia";
            default -> "producto.nombre";
        };
        Sort.Direction direccion = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direccion, campoOrden));
        return ResponseEntity.ok(service.listarConteo(
                id, buscar, soloPendientes, soloDiferencias, pageable));
    }

    @PutMapping("/{id}/conteo")
    public ResponseEntity<ConteoFisicoResumenDto> guardarConteo(
            @PathVariable Long id,
            @Valid @RequestBody GuardarConteoFisicoRequest request) {
        return ResponseEntity.ok(service.guardarConteo(id, request));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<FinalizarConteoFisicoResponse> finalizarConteo(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizarConteo(id));
    }

    @GetMapping("/{id}/diferencias")
    public ResponseEntity<Page<ConteoFisicoDetalleDto>> listarDiferencias(
            @PathVariable Long id,
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "producto") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        validarPagina(page, size);
        String campoOrden = "ubicacion".equals(sortBy) ? "ubicacion.nombre"
                : "diferencia".equals(sortBy) ? "diferencia" : "producto.nombre";
        Sort.Direction direccion = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direccion, campoOrden));
        return ResponseEntity.ok(service.listarDiferencias(id, buscar, pageable));
    }

    @PutMapping("/{id}/diferencias")
    public ResponseEntity<InventarioFisicoEstadoResponse> guardarDiferencias(
            @PathVariable Long id, @Valid @RequestBody GuardarDiferenciasRequest request) {
        return ResponseEntity.ok(service.guardarDiferencias(id, request));
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<InventarioFisicoEstadoResponse> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aprobar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<InventarioFisicoEstadoResponse> cancelar(@PathVariable Long id,
            @Valid @RequestBody CancelarInventarioFisicoRequest request) {
        return ResponseEntity.ok(service.cancelar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBorrador(@PathVariable Long id) {
        service.eliminarBorrador(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarActaPdf(@PathVariable Long id) {
        byte[] pdf = service.generarActaPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"acta_inventario_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    private void validarPagina(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("La página no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");
    }
}
