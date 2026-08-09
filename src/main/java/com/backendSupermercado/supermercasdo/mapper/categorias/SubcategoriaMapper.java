package com.backendSupermercado.supermercasdo.mapper.categorias;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Subcategoria;

@Component
public class SubcategoriaMapper {

    public SubcategoriaResponseDto toResponseDto(Subcategoria subcategoria) {
        return SubcategoriaResponseDto.builder()
                .idSubcategoria(subcategoria.getIdSubcategoria())
                .nombre(subcategoria.getNombre())
                .descripcion(subcategoria.getDescripcion())
                .estado(subcategoria.getEstado())
                .idCategoria(subcategoria.getCategoria() != null
                        ? subcategoria.getCategoria().getIdCategoria() : null)
                .nombreCategoria(subcategoria.getCategoria() != null
                        ? subcategoria.getCategoria().getNombre() : null)
                .fechaCreacion(subcategoria.getFechaCreacion())
                .fechaActualizacion(subcategoria.getFechaActualizacion())
                .build();
    }
}