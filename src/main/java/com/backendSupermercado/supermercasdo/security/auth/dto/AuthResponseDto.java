package com.backendSupermercado.supermercasdo.security.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String username;
    private String nombreCompleto;
    private String cargo;
    private Boolean controlaAsistencia;
}
