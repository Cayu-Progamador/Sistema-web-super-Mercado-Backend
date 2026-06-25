package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDetalleDto {

    private Long idUsuario;
    private String username;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

    private String nombreCompleto;
    private String ci;
    private String correo;
    private String telefono;

    private List<String> roles;
}
