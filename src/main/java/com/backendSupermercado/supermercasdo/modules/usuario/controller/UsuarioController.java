package com.backendSupermercado.supermercasdo.modules.usuario.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;
import com.backendSupermercado.supermercasdo.security.auth.dto.RegistroRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.UsuarioResponseDto;
import com.backendSupermercado.supermercasdo.security.auth.service.AuthService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    // ver perfil del usuario logueado
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDto> getProfile() {
        UsuarioPerfilDto perfil = usuarioService.getMyProfile();
        return ResponseEntity.ok(perfil);
    }

    // cambiar contrasena del usuario logueado
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @RequestBody CambiarPasswordrequestDto request) {

        usuarioService.changePassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Contraseña cambiada correctamente"));
    }

    // listar usuarios
    @GetMapping("/listar")
    public ResponseEntity<Page<UsuarioListadoResponseDto>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }

    // REGISTER nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> register(@RequestBody RegistroRequestDto request) {
        UsuarioResponseDto response = authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // poner inactivo al usuario
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);

        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "Usuario desactivado correctamente");
        response.put("status", "OK");
        
        return ResponseEntity.ok(response);
    }

    // activar el usuario
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Map<String, Object>> activar(@PathVariable Long id) {
        usuarioService.activarUsuario(id);

        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "Usuario activado correctamente");
        response.put("status", "OK");
        
        return ResponseEntity.ok(response);
    }

}
