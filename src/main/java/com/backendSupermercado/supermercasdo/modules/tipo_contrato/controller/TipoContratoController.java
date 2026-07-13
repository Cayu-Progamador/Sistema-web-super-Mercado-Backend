package com.backendSupermercado.supermercasdo.modules.tipo_contrato.controller;

import java.util.List;

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

import com.backendSupermercado.supermercasdo.modules.tipo_contrato.dto.TipoContratoDto;
import com.backendSupermercado.supermercasdo.modules.tipo_contrato.service.TipoContratoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-contrato")
@RequiredArgsConstructor
public class TipoContratoController {

    private final TipoContratoService tipoContratoService;

    @GetMapping
    public ResponseEntity<List<TipoContratoDto>> listarTodos(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(tipoContratoService.listarTodos(busqueda, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoContratoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoContratoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoContratoDto> crear(@RequestBody TipoContratoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoContratoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoContratoDto> actualizar(@PathVariable Long id, @RequestBody TipoContratoDto dto) {
        return ResponseEntity.ok(tipoContratoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<TipoContratoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(tipoContratoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<TipoContratoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(tipoContratoService.desactivar(id));
    }
}
