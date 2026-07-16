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
public class ContratoDetalleDto {

    private Long id;
    private String nroContrato;

    private ContratoEmpleadoResumenDto empleado;

    private String cargoNombre;
    private String tipoContratoNombre;
    private String tipoJornadaNombre;
    private String estado;
    private BigDecimal sueldoBase;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivoFin;
    private String observaciones;

    private String turnoNombre;
    private String horaEntrada;
    private String horaSalida;
    private Boolean lunes;
    private Boolean martes;
    private Boolean miercoles;
    private Boolean jueves;
    private Boolean viernes;
    private Boolean sabado;
    private Boolean domingo;

    private Boolean controlaAsistencia;
    private Integer horasDia;
    private Integer horasSemana;
    private Integer toleranciaMinutos;

    private String tipoPagoNombre;
}
