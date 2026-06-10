package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;

@RestController
@RequestMapping("/api/empleados")

public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/select")
    public ResponseEntity<List<EmpleadoSelectDto>> listarEmpleadoSeleccionado() {
        return ResponseEntity.ok(
            empleadoService.listarParaSelect()
        );
    }

}

