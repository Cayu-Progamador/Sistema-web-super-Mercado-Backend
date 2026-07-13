package com.backendSupermercado.supermercasdo.modules.cargo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
}
