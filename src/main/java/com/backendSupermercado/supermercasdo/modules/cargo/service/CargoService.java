package com.backendSupermercado.supermercasdo.modules.cargo.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.cargo.dto.CargoDto;

public interface CargoService {
    List<CargoDto> listarTodos(String busqueda, Boolean estado);
    CargoDto obtenerPorId(Long id);
    CargoDto crear(CargoDto dto);
    CargoDto actualizar(Long id, CargoDto dto);
    CargoDto activar(Long id);
    CargoDto desactivar(Long id);
}
