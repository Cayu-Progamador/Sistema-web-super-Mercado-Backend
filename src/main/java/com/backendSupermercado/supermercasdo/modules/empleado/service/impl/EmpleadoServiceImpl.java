package com.backendSupermercado.supermercasdo.modules.empleado.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    
    //listar los empleados para el select del usuario
    @Override
    public List<EmpleadoSelectDto> listarParaSelect() {
        return empleadoRepository.listarParaSelect();
    }

}
