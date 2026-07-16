package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContratoFiltrosDto {
    private String busqueda;
    private String estado;
    private Boolean controlaAsistencia;
    private String tipoContrato;
    private String tipoJornada;
    private Long empleadoId;
    private String fechaDesde;
    private String fechaHasta;
    private String fechaFinDesde;
    private String fechaFinHasta;
}
