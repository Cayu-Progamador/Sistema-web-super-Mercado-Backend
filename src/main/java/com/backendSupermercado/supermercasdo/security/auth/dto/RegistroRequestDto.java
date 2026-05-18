package com.backendSupermercado.supermercasdo.security.auth.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class RegistroRequestDto {
    private String username;
    private String password;
    private Long empleadoId;
    private List<String> roles;
}
