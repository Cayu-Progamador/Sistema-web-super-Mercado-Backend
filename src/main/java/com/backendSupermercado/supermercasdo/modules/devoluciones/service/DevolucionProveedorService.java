package com.backendSupermercado.supermercasdo.modules.devoluciones.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.CrearDevolucionRequestDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.DevolucionProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.RecepcionDisponibleDevolucionDto;

public interface DevolucionProveedorService {
    DevolucionProveedorResponseDto crearBorrador(Long idUsuario, CrearDevolucionRequestDto request);
    DevolucionProveedorResponseDto actualizarBorrador(Long idUsuario, Long idDevolucion, CrearDevolucionRequestDto request);
    DevolucionProveedorResponseDto confirmar(Long idUsuario, Long idDevolucion);
    DevolucionProveedorResponseDto enviar(Long idDevolucion);
    DevolucionProveedorResponseDto completar(Long idDevolucion);
    DevolucionProveedorResponseDto cancelar(Long idDevolucion);
    DevolucionProveedorResponseDto obtener(Long idDevolucion);
    List<DevolucionProveedorResponseDto> listar();
    List<String> listarEstados();
    List<RecepcionDisponibleDevolucionDto> listarRecepcionesDisponibles();
}
