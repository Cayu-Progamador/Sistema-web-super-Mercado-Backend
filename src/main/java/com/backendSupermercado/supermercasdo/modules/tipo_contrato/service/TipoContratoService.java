package com.backendSupermercado.supermercasdo.modules.tipo_contrato.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.tipo_contrato.dto.TipoContratoDto;

public interface TipoContratoService {
    List<TipoContratoDto> listarTodos(String busqueda, Boolean estado);
    TipoContratoDto obtenerPorId(Long id);
    TipoContratoDto crear(TipoContratoDto dto);
    TipoContratoDto actualizar(Long id, TipoContratoDto dto);
    TipoContratoDto activar(Long id);
    TipoContratoDto desactivar(Long id);
}
