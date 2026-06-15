package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;

public interface RolService {
    
    //listar los roles selecciona en el usuario nuevo
    public List<RolSelectDto> listarRolSeleccionado();
    
    //tabla de roles en la tabla de roles para el crud
    public List<RolDto> listarRoles();

    /// crear un nuevo rol

    public RolRespuestaDto crearRol(RolRequestDto dto);
}
