package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenovarContratoRequestDto {

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private String fechaInicio;

    private String fechaFin;

    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.01", message = "El sueldo base debe ser mayor a 0")
    private BigDecimal sueldoBase;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
