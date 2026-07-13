package com.backendSupermercado.supermercasdo.modules.turno.controller;

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

import com.backendSupermercado.supermercasdo.modules.turno.dto.TurnoDto;
import com.backendSupermercado.supermercasdo.modules.turno.service.TurnoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    public ResponseEntity<List<TurnoDto>> listarTodos(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(turnoService.listarTodos(busqueda, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TurnoDto> crear(@RequestBody TurnoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoDto> actualizar(@PathVariable Long id, @RequestBody TurnoDto dto) {
        return ResponseEntity.ok(turnoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<TurnoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<TurnoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.desactivar(id));
    }
}
