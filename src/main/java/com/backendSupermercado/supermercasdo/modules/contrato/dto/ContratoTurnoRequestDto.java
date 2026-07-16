package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoTurnoRequestDto {

    @NotNull(message = "El turno es obligatorio")
    private Long turnoId;

    private Boolean lunes = false;
    private Boolean martes = false;
    private Boolean miercoles = false;
    private Boolean jueves = false;
    private Boolean viernes = false;
    private Boolean sabado = false;
    private Boolean domingo = false;
}
