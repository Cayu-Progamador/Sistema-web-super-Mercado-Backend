package com.backendSupermercado.supermercasdo.modules.seguridad.controller;

import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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

    //lista de roles para el crud
    @GetMapping("/listar")
    public ResponseEntity<List<RolDto>> listarRoleTable() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @PostMapping("/crear")
    public ResponseEntity<RolRespuestaDto> crearRol(@RequestBody  RolRequestDto dto) {
        return ResponseEntity.ok(rolService.crearRol(dto));
    }
    
}
