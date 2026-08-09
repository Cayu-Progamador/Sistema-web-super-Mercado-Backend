package com.backendSupermercado.supermercasdo.modules.categorias.controller;

import java.util.List;
import java.util.Map;

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

import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaDetalleResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> crearCategoria(@Valid @RequestBody CategoriaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crearCategoria(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDto dto) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, dto));
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> obtenerResumenCategorias() {
        return ResponseEntity.ok(categoriaService.obtenerResumenCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDetalleResponseDto> obtenerDetalleCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerDetalleCategoria(id));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<CategoriaResponseDto>> listarCategoriasPaginado(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(categoriaService.listarCategoriasPaginado(busqueda, estado, pageable));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> listarCategorias(
            @RequestParam(required = false) String busqueda) {
        return ResponseEntity.ok(categoriaService.listarCategorias(busqueda));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaResponseDto>> listarCategoriasActivas() {
        return ResponseEntity.ok(categoriaService.listarCategoriasActivas());
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<CategoriaResponseDto> activarCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.activarCategoria(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CategoriaResponseDto> desactivarCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.desactivarCategoria(id));
    }
}
