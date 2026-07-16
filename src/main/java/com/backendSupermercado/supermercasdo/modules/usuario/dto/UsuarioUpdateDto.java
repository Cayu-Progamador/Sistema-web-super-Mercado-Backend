package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateDto {
    private String username;
    private String password;
    private List<String> roles;
}
