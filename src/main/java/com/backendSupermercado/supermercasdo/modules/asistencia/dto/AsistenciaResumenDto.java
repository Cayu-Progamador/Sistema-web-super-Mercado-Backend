package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaResumenDto {
    private long asistencias;
    private long tardanzas;
    private long faltas;
    private double puntualidad;
    private long justificados;
}
