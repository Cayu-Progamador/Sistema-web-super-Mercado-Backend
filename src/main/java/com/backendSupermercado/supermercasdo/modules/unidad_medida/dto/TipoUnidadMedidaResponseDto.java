package com.backendSupermercado.supermercasdo.modules.unidad_medida.dto;

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
public class TipoUnidadMedidaResponseDto {

    private Long idTipoUnidad;
    private String nombre;
}