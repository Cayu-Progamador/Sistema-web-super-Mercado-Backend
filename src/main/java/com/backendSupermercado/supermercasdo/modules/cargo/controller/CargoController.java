package com.backendSupermercado.supermercasdo.modules.cargo.controller;

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

import com.backendSupermercado.supermercasdo.modules.cargo.dto.CargoDto;
import com.backendSupermercado.supermercasdo.modules.cargo.service.CargoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cargos")
@RequiredArgsConstructor
public class CargoController {

    private final CargoService cargoService;

    @GetMapping
    public ResponseEntity<List<CargoDto>> listarTodos(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(cargoService.listarTodos(busqueda, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CargoDto> crear(@RequestBody CargoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cargoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoDto> actualizar(@PathVariable Long id, @RequestBody CargoDto dto) {
        return ResponseEntity.ok(cargoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<CargoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CargoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(cargoService.desactivar(id));
    }
}