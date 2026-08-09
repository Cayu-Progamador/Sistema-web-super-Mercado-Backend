package com.backendSupermercado.supermercasdo.modules.marcas.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaRequestDto;
import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaResponseDto;

public interface MarcaService {

    MarcaResponseDto crearMarca(MarcaRequestDto dto);

    MarcaResponseDto actualizarMarca(Long idMarca, MarcaRequestDto dto);

    MarcaResponseDto obtenerDetalleMarca(Long idMarca);

    Page<MarcaResponseDto> listarMarcasPaginado(String busqueda, Boolean estado, Pageable pageable);

    List<MarcaResponseDto> listarMarcas(String busqueda);

    MarcaResponseDto activarMarca(Long idMarca);

    MarcaResponseDto desactivarMarca(Long idMarca);
}