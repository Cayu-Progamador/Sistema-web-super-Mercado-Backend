package com.backendSupermercado.supermercasdo.modules.devoluciones.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.MotivoDevolucionResponseDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.MotivoDevolucionRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devoluciones")
@RequiredArgsConstructor
public class MotivoDevolucionController {

    private final MotivoDevolucionRepository motivoDevolucionRepository;

    @GetMapping("/motivos")
    public ResponseEntity<List<MotivoDevolucionResponseDto>> listarMotivos() {
        List<MotivoDevolucionResponseDto> motivos = motivoDevolucionRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .map(m -> new MotivoDevolucionResponseDto(
                        m.getIdMotivoDevolucion(),
                        m.getNombre(),
                        m.getDescripcion()))
                .toList();
        return ResponseEntity.ok(motivos);
    }
}