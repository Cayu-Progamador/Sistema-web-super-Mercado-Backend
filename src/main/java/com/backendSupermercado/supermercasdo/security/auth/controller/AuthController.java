package com.backendSupermercado.supermercasdo.security.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.security.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import com.backendSupermercado.supermercasdo.security.auth.dto.AuthResponseDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.LoginRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.RegistroRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.UsuarioResponseDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest) {
        // obtener ip
        String ip = httpRequest.getRemoteAddr();

        // login
        String token = authService.login(
                request.getUsername(),
                request.getPassword(),
                ip);
                
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> register(@RequestBody RegistroRequestDto request) {
        UsuarioResponseDto response = authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
