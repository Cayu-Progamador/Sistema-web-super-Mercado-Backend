package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPerfilDto {
    private String username;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private List<String> roles;
}
