package com.backendSupermercado.supermercasdo.modules.seguridad.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoResponseDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.ConfiguracionCorreoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/configuracion-correo")
@RequiredArgsConstructor
public class ConfiguracionCorreoController {
    private final ConfiguracionCorreoService configuracionCorreoService;

    @GetMapping
    public ResponseEntity<ConfiguracionCorreoResponseDto> obtener(Authentication authentication) {
        return ResponseEntity.ok(configuracionCorreoService.obtener(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<ConfiguracionCorreoResponseDto> guardar(
            Authentication authentication,
            @RequestBody ConfiguracionCorreoRequestDto request) {
        return ResponseEntity.ok(configuracionCorreoService.guardar(authentication.getName(), request));
    }
}
