package com.backendSupermercado.supermercasdo.modules.permiso_personal.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPermisoRequestDto {
    private Long idTipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
}
