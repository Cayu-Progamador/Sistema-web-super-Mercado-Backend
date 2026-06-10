package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;

public interface RolService {
    
    //listar los roles selecciona en el usuario nuevo
    public List<RolSelectDto> listarRolSeleccionado();
}
