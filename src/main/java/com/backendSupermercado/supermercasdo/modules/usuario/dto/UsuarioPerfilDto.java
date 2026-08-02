package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioPerfilDto {
    private String username;
    private String nombreEmpleado;
    private String apellidoEmpleado;
    private String fotoUrl;
    private List<String> roles;
    private String ci;
    private String sexo;
    private String telefono;
    private String correo;
    private String direccion;
    private String fechaContratacion;
    private String cargo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    private Boolean activo;
    private Long totalIniciosSesion;
    private Long iniciosUltimos7Dias;
    private Long iniciosUltimos30Dias;
}
