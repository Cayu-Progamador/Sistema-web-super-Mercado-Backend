package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@AllArgsConstructor
public class UsuarioListadoResponseDto {

    private Long idUsuario;

    private String nombreCompleto;

    private String username;

    private String correo;

    private String rol;

    private Boolean activo;

    private LocalDateTime ultimoAcceso;

   

}
