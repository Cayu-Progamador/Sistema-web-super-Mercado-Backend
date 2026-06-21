package com.backendSupermercado.supermercasdo.modules.empleado.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    
    
    //listar los empleados para el select del usuario (solo sin usuario asignado)
    @Override
    public List<EmpleadoSelectDto> listarParaSelect() {
        return empleadoRepository.listarParaSelect();
    }

    //listar empleados para editar (excluye los asignados a OTROS usuarios)
    @Override
    public List<EmpleadoSelectDto> listarParaEditar(Long usuarioId) {
        return empleadoRepository.listarParaEditar(usuarioId);
    }

}
