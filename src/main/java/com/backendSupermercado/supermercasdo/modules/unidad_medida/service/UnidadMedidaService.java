package com.backendSupermercado.supermercasdo.modules.unidad_medida.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.TipoUnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaRequestDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaResponseDto;

public interface UnidadMedidaService {

    UnidadMedidaResponseDto crearUnidadMedida(UnidadMedidaRequestDto dto);

    UnidadMedidaResponseDto actualizarUnidadMedida(Long idUnidadMedida, UnidadMedidaRequestDto dto);

    UnidadMedidaResponseDto obtenerDetalleUnidadMedida(Long idUnidadMedida);

    List<UnidadMedidaResponseDto> listarUnidadesMedida(String busqueda);

    Page<UnidadMedidaResponseDto> listarUnidadesMedidaPaginado(String busqueda, Boolean estado, Long idTipoUnidad, Pageable pageable);

    UnidadMedidaResponseDto activarUnidadMedida(Long idUnidadMedida);

    UnidadMedidaResponseDto desactivarUnidadMedida(Long idUnidadMedida);

    List<TipoUnidadMedidaResponseDto> listarTiposUnidadMedida();
}