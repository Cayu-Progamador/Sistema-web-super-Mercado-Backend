package com.backendSupermercado.supermercasdo.modules.configuracion.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaRequestDto;
import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaResponseDto;
import com.backendSupermercado.supermercasdo.modules.configuracion.service.ConfiguracionEmpresaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/configuracion-empresa")
@RequiredArgsConstructor
public class ConfiguracionEmpresaController {
    private final ConfiguracionEmpresaService configuracionEmpresaService;

    @GetMapping
    public ResponseEntity<ConfiguracionEmpresaResponseDto> obtener() {
        return ResponseEntity.ok(configuracionEmpresaService.obtener());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConfiguracionEmpresaResponseDto> guardar(
            @RequestPart("datos") ConfiguracionEmpresaRequestDto datos,
            @RequestPart(value = "logo", required = false) MultipartFile logo,
            @RequestPart(value = "fondoLogin", required = false) MultipartFile fondoLogin) {
        return ResponseEntity.ok(configuracionEmpresaService.guardar(datos, logo, fondoLogin));
    }
}
