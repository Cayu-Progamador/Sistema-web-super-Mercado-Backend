package com.backendSupermercado.supermercasdo.security.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.security.auth.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ForgotPasswordRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ResetPasswordRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.AuthResponseDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.LoginRequestDto;


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

    
    // Mandar correo de recuperación
    @PostMapping("/forgot-password")
    public ResponseEntity<String> recuperarContrasena(
            @RequestBody ForgotPasswordRequestDto request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "Correo de recuperación enviado");
    }

    // Verificar PIN
    @PostMapping("/verify-pin")
    public ResponseEntity<String> verifyPin(
            @RequestBody ResetPasswordRequestDto request) {

        authService.verifyPin(request.getEmail(), request.getPin());

        return ResponseEntity.ok("PIN correcto");
    }

    // Resetear contraseña
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequestDto request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                "Contraseña reseteada correctamente");
    }

    //reembiar nuevo codigo para el usuario
    @PostMapping("/resend-code")
    public ResponseEntity<String> reembirarCodigo(
            @RequestBody ResetPasswordRequestDto request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                "Código reenviado correctamente");
    }
}
