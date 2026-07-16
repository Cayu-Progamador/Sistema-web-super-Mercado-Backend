package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoListadoDto {
    private Long id;
    private ContratoEmpleadoResumenDto empleado;
    private String cargoNombre;
    private Long cargoId;
    private String tipoContratoNombre;
    private Long tipoContratoId;
    private String tipoJornadaNombre;
    private Long tipoJornadaId;
    private BigDecimal sueldoBase;
    private Integer horasDia;
    private Integer horasSemana;
    private Boolean controlaAsistencia;
    private Integer toleranciaMinutos;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private Long tipoPagoId;
}
