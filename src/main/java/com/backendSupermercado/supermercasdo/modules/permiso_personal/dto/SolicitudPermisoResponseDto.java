package com.backendSupermercado.supermercasdo.modules.permiso_personal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPermisoResponseDto {
    private Long id;
    private Long idEmpleado;
    private String nombreEmpleado;
    private Long idTipo;
    private String nombreTipo;
    private Long idEstado;
    private String nombreEstado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private String comentarioAdmin;
    private String nombreCargo;
    private LocalDateTime createdAt;
}
