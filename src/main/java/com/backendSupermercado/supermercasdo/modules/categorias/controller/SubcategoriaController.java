package com.backendSupermercado.supermercasdo.modules.categorias.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaConProductosResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.service.SubcategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subcategorias")
@RequiredArgsConstructor
public class SubcategoriaController {

    private final SubcategoriaService subcategoriaService;

    @PostMapping
    public ResponseEntity<SubcategoriaResponseDto> crearSubcategoria(@Valid @RequestBody SubcategoriaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subcategoriaService.crearSubcategoria(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubcategoriaResponseDto> actualizarSubcategoria(
            @PathVariable Long id,
            @Valid @RequestBody SubcategoriaRequestDto dto) {
        return ResponseEntity.ok(subcategoriaService.actualizarSubcategoria(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaConProductosResponseDto> obtenerDetalleSubcategoria(@PathVariable Long id) {
        return ResponseEntity.ok(subcategoriaService.obtenerDetalleSubcategoria(id));
    }

    @GetMapping
    public ResponseEntity<List<SubcategoriaResponseDto>> listarSubcategorias(
            @RequestParam(required = false) Long idCategoria) {
        return ResponseEntity.ok(subcategoriaService.listarSubcategoriasPorCategoria(idCategoria));
    }
}
