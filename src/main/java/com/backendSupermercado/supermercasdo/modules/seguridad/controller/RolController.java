package com.backendSupermercado.supermercasdo.modules.seguridad.controller;

import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;
    //lsiatr los roles para el nuevo usuario
    @GetMapping("/select-rol")
    public ResponseEntity<List<RolSelectDto>> selectRol() {
        return ResponseEntity.ok(
          rolService.listarRolSeleccionado()  
        );
    }
    
}
