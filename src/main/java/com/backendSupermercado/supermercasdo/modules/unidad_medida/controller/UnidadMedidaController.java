package com.backendSupermercado.supermercasdo.modules.unidad_medida.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.TipoUnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaRequestDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.service.UnidadMedidaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unidades-medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    @PostMapping
    public ResponseEntity<UnidadMedidaResponseDto> crearUnidadMedida(@Valid @RequestBody UnidadMedidaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadMedidaService.crearUnidadMedida(dto));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<UnidadMedidaResponseDto>> listarUnidadesMedidaPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idTipoUnidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(unidadMedidaService.listarUnidadesMedidaPaginado(busqueda, estado, idTipoUnidad, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDto> actualizarUnidadMedida(
            @PathVariable Long id,
            @Valid @RequestBody UnidadMedidaRequestDto dto) {
        return ResponseEntity.ok(unidadMedidaService.actualizarUnidadMedida(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDto> obtenerDetalleUnidadMedida(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.obtenerDetalleUnidadMedida(id));
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoUnidadMedidaResponseDto>> listarTiposUnidadMedida() {
        return ResponseEntity.ok(unidadMedidaService.listarTiposUnidadMedida());
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponseDto>> listarUnidadesMedida(
            @RequestParam(required = false) String busqueda) {
        return ResponseEntity.ok(unidadMedidaService.listarUnidadesMedida(busqueda));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<UnidadMedidaResponseDto> activarUnidadMedida(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.activarUnidadMedida(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UnidadMedidaResponseDto> desactivarUnidadMedida(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.desactivarUnidadMedida(id));
    }
}