package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class ContratoRequestDto {

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    @NotNull(message = "El cargo es obligatorio")
    private Long cargoId;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private Long tipoContratoId;

    @NotNull(message = "El tipo de jornada es obligatorio")
    private Long tipoJornadaId;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    private String fechaInicio;

    private String fechaFin;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    @NotNull(message = "El sueldo base es obligatorio")
    @DecimalMin(value = "0.01", message = "El sueldo base debe ser mayor a 0")
    private BigDecimal sueldoBase;

    @Min(value = 1, message = "Las horas por día deben ser al menos 1")
    private Integer horasDia;

    @Min(value = 1, message = "Las horas por semana deben ser al menos 1")
    private Integer horasSemana;

    private Boolean controlaAsistencia;

    @Min(value = 0, message = "La tolerancia no puede ser negativa")
    private Integer toleranciaMinutos;

    private Long tipoPagoId;

    @Valid
    private ContratoTurnoRequestDto contratoTurno;

    @NotBlank(message = "El estado es obligatorio")
    @Size(min = 3, max = 20, message = "El estado debe tener entre 3 y 20 caracteres")
    private String estado;
}
