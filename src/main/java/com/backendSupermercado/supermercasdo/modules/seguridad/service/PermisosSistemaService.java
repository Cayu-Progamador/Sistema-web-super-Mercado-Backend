package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.PermisosMatrizDto;

public interface PermisosSistemaService {
    PermisosMatrizDto.Respuesta obtenerMatriz(Long idRol);
    void guardarMatriz(Long idRol, PermisosMatrizDto.GuardarRequest request);
}
