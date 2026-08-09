package com.backendSupermercado.supermercasdo.modules.unidad_medida.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadMedidaResponseDto {

    private Long idUnidadMedida;
    private String nombre;
    private String abreviatura;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Long idTipoUnidad;
    private String nombreTipoUnidad;
}