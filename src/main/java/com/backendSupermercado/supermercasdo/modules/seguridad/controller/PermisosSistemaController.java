package com.backendSupermercado.supermercasdo.modules.seguridad.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.PermisosMatrizDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.PermisosSistemaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permisos-sistema")
public class PermisosSistemaController {
    private final PermisosSistemaService service;

    @GetMapping("/mis-permisos")
    public ResponseEntity<java.util.List<String>> misPermisos(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList());
    }

    @GetMapping("/matriz/{idRol}")
    public ResponseEntity<PermisosMatrizDto.Respuesta> obtenerMatriz(@PathVariable Long idRol) {
        return ResponseEntity.ok(service.obtenerMatriz(idRol));
    }

    @PutMapping("/matriz/{idRol}")
    public ResponseEntity<Void> guardarMatriz(@PathVariable Long idRol,
            @RequestBody PermisosMatrizDto.GuardarRequest request) {
        service.guardarMatriz(idRol, request);
        return ResponseEntity.noContent().build();
    }
}
