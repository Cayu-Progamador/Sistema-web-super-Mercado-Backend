package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaJustificarRequestDto {
    private String tipoJustificacion;
    private String motivo;
}
