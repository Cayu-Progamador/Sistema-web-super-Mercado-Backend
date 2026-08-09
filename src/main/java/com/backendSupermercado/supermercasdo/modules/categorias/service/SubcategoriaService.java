package com.backendSupermercado.supermercasdo.modules.categorias.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaConProductosResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaResponseDto;

public interface SubcategoriaService {

    SubcategoriaResponseDto crearSubcategoria(SubcategoriaRequestDto dto);

    SubcategoriaResponseDto actualizarSubcategoria(Long idSubcategoria, SubcategoriaRequestDto dto);

    SubcategoriaConProductosResponseDto obtenerDetalleSubcategoria(Long idSubcategoria);

    List<SubcategoriaResponseDto> listarSubcategoriasPorCategoria(Long idCategoria);
}
