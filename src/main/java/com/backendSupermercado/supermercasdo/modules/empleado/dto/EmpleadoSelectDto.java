package com.backendSupermercado.supermercasdo.modules.empleado.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSelectDto {
    private Long id;
    private String nombreCompleto;
}
