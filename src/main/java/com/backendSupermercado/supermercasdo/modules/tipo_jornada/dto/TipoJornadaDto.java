package com.backendSupermercado.supermercasdo.modules.tipo_jornada.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoJornadaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}
