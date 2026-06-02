package com.backendSupermercado.supermercasdo.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
@ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        // Creamos un objeto JSON con el mensaje exacto que tú escribiste
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        // Lo devolvemos con un status 401 (Unauthorized)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Map<String, String>> handleUsuarioBloqueadoException(ResourceConflictException ex) {
        Map<String, String> response = new HashMap<>();
        // Vue seguirá leyendo esto sin problemas:
        response.put("message", ex.getMessage()); 
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
