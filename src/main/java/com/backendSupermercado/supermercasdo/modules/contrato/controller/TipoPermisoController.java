package com.backendSupermercado.supermercasdo.modules.contrato.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.contrato.repository.TipoPermisoRepository;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.TipoPermiso;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-permiso")
@RequiredArgsConstructor
public class TipoPermisoController {

    private final TipoPermisoRepository tipoPermisoRepository;

    @GetMapping
    public ResponseEntity<List<TipoPermiso>> listar() {
        return ResponseEntity.ok(tipoPermisoRepository.findByEstado(true));
    }
}
