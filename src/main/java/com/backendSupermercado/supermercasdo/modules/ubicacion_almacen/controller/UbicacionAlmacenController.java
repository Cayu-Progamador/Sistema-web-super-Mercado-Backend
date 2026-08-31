package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.UbicacionAlmacenResponseDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.TipoUbicacionRepository;

import lombok.RequiredArgsConstructor;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service.UbicacionAlmacenService;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.UbicacionListadoDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ActualizarUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ProductoUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.CrearUbicacionDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

@RestController
@RequestMapping("/api/almacenes/ubicaciones")
@RequiredArgsConstructor
public class UbicacionAlmacenController {

    private final UbicacionAlmacenRepository ubicacionAlmacenRepository;
    private final UbicacionAlmacenService ubicacionService;
    private final TipoUbicacionRepository tipoUbicacionRepository;

    @GetMapping("/tipos")
    public ResponseEntity<List<Map<String, String>>> listarTipos() {
        return ResponseEntity.ok(tipoUbicacionRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(t -> Map.of("label", t.getNombre().replace('_', ' '), "value", t.getNombre(),
                        "descripcion", Objects.requireNonNullElse(t.getDescripcion(), "")))
                .toList());
    }

    /** Catálogo para que el frontend no defina los estados de ubicación localmente. */
    @GetMapping("/estados")
    public ResponseEntity<List<Map<String, String>>> listarEstados() {
        return ResponseEntity.ok(List.of(
                Map.of("label", "Activo", "value", "ACTIVO"),
                Map.of("label", "Inactivo", "value", "INACTIVO")
        ));
    }

    @GetMapping("/pagina")
    public ResponseEntity<Page<UbicacionListadoDto>> listarPaginado(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long idAlmacen,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(ubicacionService.listar(search, idAlmacen, tipo, estado,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("nombre").ascending())));
    }

    @PostMapping
    public ResponseEntity<UbicacionListadoDto> crear(@Valid @RequestBody CrearUbicacionDto dto) {
        return ResponseEntity.status(201).body(ubicacionService.crear(dto));
    }

    @GetMapping("/{id}/inventario/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails user) {
        byte[] contenido = ubicacionService.generarPdf(id, user != null ? user.getUsername() : "Sistema");
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Inventario_Ubicacion_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF).body(contenido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionListadoDto> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarUbicacionDto dto) { return ResponseEntity.ok(ubicacionService.actualizar(id, dto)); }
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UbicacionListadoDto> activar(@PathVariable Long id) { return ResponseEntity.ok(ubicacionService.activar(id)); }
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UbicacionListadoDto> desactivar(@PathVariable Long id) { return ResponseEntity.ok(ubicacionService.desactivar(id)); }

    @GetMapping("/{id}/inventario")
    public ResponseEntity<List<ProductoUbicacionDto>> inventario(@PathVariable Long id) { return ResponseEntity.ok(ubicacionService.listarProductos(id)); }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<UbicacionAlmacenResponseDto>> listarUbicaciones(
            @RequestParam(required = false) Long idAlmacen) {
        var resultado = idAlmacen == null
                ? ubicacionAlmacenRepository.findAll()
                : ubicacionAlmacenRepository.findByAlmacen_IdAlmacenOrderByNombreAsc(idAlmacen);
        List<UbicacionAlmacenResponseDto> ubicaciones = resultado.stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .map(u -> new UbicacionAlmacenResponseDto(
                        u.getIdUbicacion(),
                        u.getCodigo(),
                        u.getNombre(),
                        u.getAlmacen() != null ? u.getAlmacen().getIdAlmacen() : null,
                        u.getAlmacen() != null ? u.getAlmacen().getNombre() : null,
                        u.getAlmacen() != null && u.getAlmacen().getTipoAlmacen() != null
                                ? u.getAlmacen().getTipoAlmacen().getNombre()
                                : null))
                .toList();
        return ResponseEntity.ok(ubicaciones);
    }
}
