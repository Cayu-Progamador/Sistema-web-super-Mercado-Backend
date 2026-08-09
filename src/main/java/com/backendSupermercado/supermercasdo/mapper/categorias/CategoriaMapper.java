package com.backendSupermercado.supermercasdo.mapper.categorias;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;

@Component
public class CategoriaMapper {

    public CategoriaResponseDto toResponseDto(Categoria categoria) {
        return CategoriaResponseDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.getEstado())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}