package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AusenteDto {
    private Long id;
    private String nombre;
    private String cargo;
    private String telefono;
}
