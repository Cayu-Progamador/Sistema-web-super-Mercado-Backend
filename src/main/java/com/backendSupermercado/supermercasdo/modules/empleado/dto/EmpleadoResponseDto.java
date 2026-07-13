package com.backendSupermercado.supermercasdo.modules.empleado.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResponseDto {
    private Long idEmpleado;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto;
    private String ci;
    private String fechaNacimiento;
    private String fechaContratacion;
    private String correo;
    private String telefono;
    private Long idSexo;
    private String sexo;
    private Long idCargo;
    private String cargo;
    private Boolean estado;
    private String zona;
    private String calle;
    private String numero;
    private String referencia;
    private String pais;
    private String departamento;
    private String ciudad;
}
