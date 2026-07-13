package com.backendSupermercado.supermercasdo.modules.tipo_pago.controller;

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

import com.backendSupermercado.supermercasdo.modules.tipo_pago.dto.TipoPagoDto;
import com.backendSupermercado.supermercasdo.modules.tipo_pago.service.TipoPagoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private final TipoPagoService tipoPagoService;

    @GetMapping
    public ResponseEntity<List<TipoPagoDto>> listarTodos(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado) {
        return ResponseEntity.ok(tipoPagoService.listarTodos(busqueda, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPagoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoPagoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TipoPagoDto> crear(@RequestBody TipoPagoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoPagoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoPagoDto> actualizar(@PathVariable Long id, @RequestBody TipoPagoDto dto) {
        return ResponseEntity.ok(tipoPagoService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<TipoPagoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(tipoPagoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<TipoPagoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(tipoPagoService.desactivar(id));
    }
}
