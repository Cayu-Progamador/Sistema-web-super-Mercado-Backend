package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import java.util.Map;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository.EstadoTransferenciaRepository;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository.TransferenciaAlmacenRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.TransferenciaListadoDto;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.CrearTransferenciaRequest;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service.TransferenciaAlmacenService;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service.TransferenciaPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transferencias")
@RequiredArgsConstructor
public class TransferenciaAlmacenController {
    private final TransferenciaAlmacenService service;
    private final EstadoTransferenciaRepository estadoRepository;
    private final TransferenciaPdfService pdfService;
    private final TransferenciaAlmacenRepository transferenciaRepository;

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        var t = transferenciaRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Transferencia no encontrada"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Transferencia_" + t.getCodigo() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF).body(pdfService.generar(t));
    }

    @GetMapping("/estados")
    public ResponseEntity<List<Map<String, String>>> estados() {
        return ResponseEntity.ok(estadoRepository.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                .sorted(java.util.Comparator.comparing(e -> e.getNombre()))
                .map(e -> Map.of("label", e.getNombre(), "value", e.getNombre()))
                .toList());
    }

    @PostMapping
    public ResponseEntity<TransferenciaListadoDto> crear(@Valid @RequestBody CrearTransferenciaRequest request) {
        return ResponseEntity.status(201).body(service.crear(request, false));
    }

    @PostMapping("/borradores")
    public ResponseEntity<TransferenciaListadoDto> crearBorrador(@RequestBody CrearTransferenciaRequest request) {
        return ResponseEntity.status(201).body(service.crear(request, true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransferenciaListadoDto> actualizarBorrador(@PathVariable Long id,
            @RequestBody CrearTransferenciaRequest request) {
        return ResponseEntity.ok(service.actualizarBorrador(id, request));
    }

    @PatchMapping("/{id}/procesar")
    public ResponseEntity<TransferenciaListadoDto> procesar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cambiarEstado(id, "EN_PROCESO"));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<TransferenciaListadoDto> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirmarBorrador(id));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<TransferenciaListadoDto> completar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cambiarEstado(id, "COMPLETADA"));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TransferenciaListadoDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cambiarEstado(id, "CANCELADA"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBorrador(@PathVariable Long id) {
        service.eliminarBorrador(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TransferenciaListadoDto>> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String responsable,
            @RequestParam(required = false) Long almacenOrigenId,
            @RequestParam(required = false) Long almacenDestinoId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaTransferencia") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        String campo = switch (sortBy) {
            case "codigo" -> "codigo";
            case "estado" -> "estadoTransferencia.nombre";
            case "origen" -> "ubicacionOrigen.almacen.nombre";
            case "destino" -> "ubicacionDestino.almacen.nombre";
            default -> "fechaTransferencia";
        };
        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(campo).ascending() : Sort.by(campo).descending();
        int pagina = Math.max(page, 0);
        int tamano = Math.min(Math.max(size, 1), 100);
        return ResponseEntity
                .ok(service.listar(texto, estado, usuarioId, responsable, almacenOrigenId, almacenDestinoId,
                        parse(fechaDesde), parse(fechaHasta), PageRequest.of(pagina, tamano, sort)));
    }

    private LocalDate parse(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha inválida: " + value);
        }
    }
}
