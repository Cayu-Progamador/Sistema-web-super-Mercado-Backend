package com.backendSupermercado.supermercasdo.modules.empleado.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;

public interface EmpleadoService {
    
    //listar los empleados para el select del usuario
    public List<EmpleadoSelectDto> listarParaSelect();
    public List<EmpleadoSelectDto> listarParaEditar(Long usuarioId);
}
