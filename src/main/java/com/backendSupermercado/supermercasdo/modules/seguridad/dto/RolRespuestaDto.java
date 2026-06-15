package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolRespuestaDto {
    private Long idRol;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}
