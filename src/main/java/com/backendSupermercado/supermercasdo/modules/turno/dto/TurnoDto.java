package com.backendSupermercado.supermercasdo.modules.turno.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurnoDto {
    private Long id;
    private String nombre;
    private String horaEntrada;
    private String horaSalida;
    private Boolean estado;
}
