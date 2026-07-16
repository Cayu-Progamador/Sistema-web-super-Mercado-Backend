package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoEmpleadoResumenDto {
    private Long id;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String email;
    private String telefono;
    private String direccion;
}
