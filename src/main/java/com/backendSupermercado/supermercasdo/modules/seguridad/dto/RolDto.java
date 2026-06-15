package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolDto {
    private Long idRol;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private Integer cantidadUsuarios;
}
