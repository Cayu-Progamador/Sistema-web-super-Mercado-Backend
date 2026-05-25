package com.backendSupermercado.supermercasdo.modules.usuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //ver perfil del usuario logueado
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDto> getProfile() {
        UsuarioPerfilDto perfil = usuarioService.getMyProfile();
        return ResponseEntity.ok(perfil);
    }
}
