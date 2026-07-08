package com.backendSupermercado.supermercasdo.modules.empleado.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;

public interface EmpleadoService {
    
    public List<EmpleadoSelectDto> listarParaSelect();
    public List<EmpleadoSelectDto> listarParaEditar(Long usuarioId);
    public Page<EmpleadoListadoDto> listarEmpleados(String busqueda, Boolean estado, Pageable pageable);
}
