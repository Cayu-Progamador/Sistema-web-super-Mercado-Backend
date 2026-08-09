package com.backendSupermercado.supermercasdo.modules.categorias.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaDetalleResponseDto;

public interface CategoriaService {

    CategoriaResponseDto crearCategoria(CategoriaRequestDto dto);

    CategoriaResponseDto actualizarCategoria(Long idCategoria, CategoriaRequestDto dto);

    CategoriaDetalleResponseDto obtenerDetalleCategoria(Long idCategoria);

    Map<String, Long> obtenerResumenCategorias();

    List<CategoriaResponseDto> listarCategorias(String busqueda);

    List<CategoriaResponseDto> listarCategoriasActivas();

    Page<CategoriaResponseDto> listarCategoriasPaginado(String busqueda, Boolean estado, Pageable pageable);

    CategoriaResponseDto activarCategoria(Long idCategoria);

    CategoriaResponseDto desactivarCategoria(Long idCategoria);
}
