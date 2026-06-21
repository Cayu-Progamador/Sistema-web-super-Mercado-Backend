package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping("/select")
    public ResponseEntity<List<EmpleadoSelectDto>> listarEmpleadoSeleccionado() {
        return ResponseEntity.ok(
            empleadoService.listarParaSelect()
        );
    }

    @GetMapping("/editar/{usuarioId}")
    public ResponseEntity<List<EmpleadoSelectDto>> listarParaEditar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(
            empleadoService.listarParaEditar(usuarioId)
        );
    }

}

