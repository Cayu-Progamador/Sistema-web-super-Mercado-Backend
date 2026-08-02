package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaCrearRequestDto {
    private Long idContrato;
    private String fecha;
    private String horaEntrada;
    private String horaSalida;
    private String estado;
}
