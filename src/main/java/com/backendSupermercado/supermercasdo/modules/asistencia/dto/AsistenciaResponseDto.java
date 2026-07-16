package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsistenciaResponseDto {
    private Long idAsistencia;
    private Long idEmpleado;
    private String nombreEmpleado;
    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private String estado;
    private Integer minutosRetraso;
    private String tipoJustificacion;
    private String motivoJustificacion;

    private LocalTime horaEntradaEsperada;
    private LocalTime horaSalidaEsperada;
    private Integer toleranciaMinutos;
    private String turnoNombre;

    public AsistenciaResponseDto(Long idAsistencia, Long idEmpleado, String nombreEmpleado,
                                  LocalDate fecha, LocalTime horaEntrada, LocalTime horaSalida,
                                  String estado, Integer minutosRetraso) {
        this.idAsistencia = idAsistencia;
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.estado = estado;
        this.minutosRetraso = minutosRetraso;
    }
}
