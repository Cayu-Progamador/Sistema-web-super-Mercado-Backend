package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RolRepository rolRepository;
    
    //listar roles en el nuevo usuario
    @Override
    public List<RolSelectDto> listarRolSeleccionado() {
        return rolRepository.listarRolSelect();
    }

}
