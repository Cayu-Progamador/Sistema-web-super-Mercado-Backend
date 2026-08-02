package com.backendSupermercado.supermercasdo.modules.permiso_personal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialSolicitudDto {
    private Long id;
    private Long idSolicitud;
    private String estadoAnterior;
    private String estadoNuevo;
    private Long idUsuarioAccion;
    private String nombreUsuario;
    private String comentario;
    private LocalDateTime fechaCambio;
}
