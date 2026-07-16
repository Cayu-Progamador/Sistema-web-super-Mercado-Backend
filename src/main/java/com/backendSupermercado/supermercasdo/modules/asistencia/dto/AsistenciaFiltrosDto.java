package com.backendSupermercado.supermercasdo.modules.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaFiltrosDto {
    private Long idContrato;
    private String fechaDesde;
    private String fechaHasta;
    private String estado;
}
