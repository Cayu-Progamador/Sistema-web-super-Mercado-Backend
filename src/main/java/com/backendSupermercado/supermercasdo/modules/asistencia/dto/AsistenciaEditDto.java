package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsistenciaEditDto {
    private String horaEntrada;
    private String horaSalida;
    private String estado;
}
