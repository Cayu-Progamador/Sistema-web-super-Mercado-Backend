package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RolSelectDto {
    private String nombre;
    private String descripcion;
}
