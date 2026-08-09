package com.backendSupermercado.supermercasdo.mapper.unidad_medida;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.TipoUnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.TipoUnidadMedida;

@Component
public class TipoUnidadMedidaMapper {

    public TipoUnidadMedidaResponseDto toResponseDto(TipoUnidadMedida tipoUnidadMedida) {
        return TipoUnidadMedidaResponseDto.builder()
                .idTipoUnidad(tipoUnidadMedida.getIdTipoUnidad())
                .nombre(tipoUnidadMedida.getNombre())
                .build();
    }
}