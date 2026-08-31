package com.backendSupermercado.supermercasdo.modules.almacen.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenListadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.TipoAlmacenDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenCreadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.CrearAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ActualizarAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ResponsableAlmacenDto;
import com.backendSupermercado.supermercasdo.modules.almacen.service.AlmacenService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

@RestController
@RequestMapping("/api/almacenes")
@RequiredArgsConstructor
public class AlmacenController {

    private static final Set<String> CAMPOS_ORDENABLES = Set.of("idAlmacen", "codigo", "nombre", "activo");
    private final AlmacenService almacenService;

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoAlmacenDto>> listarTipos() {
        return ResponseEntity.ok(almacenService.listarTiposActivos());
    }

    @GetMapping("/responsables")
    public ResponseEntity<List<ResponsableAlmacenDto>> listarResponsables() {
        return ResponseEntity.ok(almacenService.listarResponsablesActivos());
    }

    @PostMapping
    public ResponseEntity<AlmacenCreadoDto> crear(@Valid @RequestBody CrearAlmacenRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(almacenService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlmacenCreadoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarAlmacenRequestDto dto) {
        return ResponseEntity.ok(almacenService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<AlmacenCreadoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(almacenService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<AlmacenCreadoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(almacenService.desactivar(id));
    }

    @GetMapping("/{id}/inventario/pdf")
    public ResponseEntity<byte[]> descargarInventarioPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String generadoPor = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = almacenService.generarInventarioPdf(id, generadoPor);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Inventario_Almacen_" + id + ".pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping
    public ResponseEntity<Page<AlmacenListadoDto>> listar(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String campoOrden = CAMPOS_ORDENABLES.contains(sortBy) ? sortBy : "nombre";
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(campoOrden).descending()
                : Sort.by(campoOrden).ascending();
        Pageable pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), sort);
        return ResponseEntity.ok(almacenService.listar(search, tipo, estado, pageable));
    }
}
