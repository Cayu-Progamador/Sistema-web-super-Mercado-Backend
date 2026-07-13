package com.backendSupermercado.supermercasdo.modules.tipo_jornada.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.tipo_jornada.dto.TipoJornadaDto;

public interface TipoJornadaService {
    List<TipoJornadaDto> listarTodos(String busqueda, Boolean estado);
    TipoJornadaDto obtenerPorId(Long id);
    TipoJornadaDto crear(TipoJornadaDto dto);
    TipoJornadaDto actualizar(Long id, TipoJornadaDto dto);
    TipoJornadaDto activar(Long id);
    TipoJornadaDto desactivar(Long id);
}
