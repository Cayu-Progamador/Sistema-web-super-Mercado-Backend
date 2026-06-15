package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDetalleDto {

    //informacion de usuario
    private String username;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

    //empleado asociado
    private String nombreEmpleado;
    private String correo;

    //roles
    private List<String> roles;
}
