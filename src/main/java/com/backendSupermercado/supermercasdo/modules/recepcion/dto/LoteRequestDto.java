package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteRequestDto {

    private String codigoLote;

    private LocalDate fechaFabricacion;

    private LocalDate fechaVencimiento;
}