package com.backendSupermercado.supermercasdo.modules.turno.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.turno.dto.TurnoDto;

public interface TurnoService {
    List<TurnoDto> listarTodos(String busqueda, Boolean estado);
    TurnoDto obtenerPorId(Long id);
    TurnoDto crear(TurnoDto dto);
    TurnoDto actualizar(Long id, TurnoDto dto);
    TurnoDto activar(Long id);
    TurnoDto desactivar(Long id);
}
