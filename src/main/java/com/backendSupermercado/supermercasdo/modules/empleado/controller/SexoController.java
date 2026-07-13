package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.SexoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.SexoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sexos")
@RequiredArgsConstructor
public class SexoController {

    private final SexoRepository sexoRepository;

    @GetMapping
    public ResponseEntity<List<SexoDto>> listarSexos() {
        return ResponseEntity.ok(
            sexoRepository.findAll().stream()
                .map(s -> new SexoDto(s.getIdSexo(), s.getNombre()))
                .toList()
        );
    }
}
