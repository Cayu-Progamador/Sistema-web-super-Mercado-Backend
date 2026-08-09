package com.backendSupermercado.supermercasdo.mapper.unidad_medida;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;

@Component
public class UnidadMedidaMapper {

    public UnidadMedidaResponseDto toResponseDto(UnidadMedida unidadMedida) {
        return UnidadMedidaResponseDto.builder()
                .idUnidadMedida(unidadMedida.getIdUnidadMedida())
                .nombre(unidadMedida.getNombre())
                .abreviatura(unidadMedida.getAbreviatura())
                .estado(unidadMedida.getEstado())
                .fechaCreacion(unidadMedida.getFechaCreacion())
                .fechaActualizacion(unidadMedida.getFechaActualizacion())
                .idTipoUnidad(unidadMedida.getTipoUnidadMedida() != null
                        ? unidadMedida.getTipoUnidadMedida().getIdTipoUnidad() : null)
                .nombreTipoUnidad(unidadMedida.getTipoUnidadMedida() != null
                        ? unidadMedida.getTipoUnidadMedida().getNombre() : null)
                .build();
    }
}