package com.backendSupermercado.supermercasdo.mapper.marcas;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaResponseDto;
import com.backendSupermercado.supermercasdo.modules.marcas.entity.Marca;

@Component
public class MarcaMapper {

    public MarcaResponseDto toResponseDto(Marca marca) {
        return MarcaResponseDto.builder()
                .idMarca(marca.getIdMarca())
                .nombre(marca.getNombre())
                .descripcion(marca.getDescripcion())
                .estado(marca.getEstado())
                .fechaCreacion(marca.getFechaCreacion())
                .fechaActualizacion(marca.getFechaActualizacion())
                .cantidadProductos(marca.getProductos() != null ? (long) marca.getProductos().size() : 0L)
                .build();
    }
}