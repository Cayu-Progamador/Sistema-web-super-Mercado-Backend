package com.backendSupermercado.supermercasdo.modules.marcas.controller;

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

import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaRequestDto;
import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaResponseDto;
import com.backendSupermercado.supermercasdo.modules.marcas.service.MarcaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    public ResponseEntity<MarcaResponseDto> crearMarca(@Valid @RequestBody MarcaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.crearMarca(dto));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<MarcaResponseDto>> listarMarcasPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(marcaService.listarMarcasPaginado(busqueda, estado, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcaResponseDto> actualizarMarca(
            @PathVariable Long id,
            @Valid @RequestBody MarcaRequestDto dto) {
        return ResponseEntity.ok(marcaService.actualizarMarca(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaResponseDto> obtenerDetalleMarca(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.obtenerDetalleMarca(id));
    }

    @GetMapping
    public ResponseEntity<List<MarcaResponseDto>> listarMarcas(
            @RequestParam(required = false) String busqueda) {
        return ResponseEntity.ok(marcaService.listarMarcas(busqueda));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<MarcaResponseDto> activarMarca(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.activarMarca(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<MarcaResponseDto> desactivarMarca(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.desactivarMarca(id));
    }
}