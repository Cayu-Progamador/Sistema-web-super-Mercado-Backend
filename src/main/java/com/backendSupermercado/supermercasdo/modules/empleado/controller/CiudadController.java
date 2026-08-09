package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.CiudadDto;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.CiudadRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadRepository ciudadRepository;

    @GetMapping
    public ResponseEntity<List<CiudadDto>> listarCiudades() {
        return ResponseEntity.ok(
            ciudadRepository.findAll().stream()
                .map(c -> new CiudadDto(c.getIdCiudad(), c.getNombre()))
                .toList()
        );
    }
}
