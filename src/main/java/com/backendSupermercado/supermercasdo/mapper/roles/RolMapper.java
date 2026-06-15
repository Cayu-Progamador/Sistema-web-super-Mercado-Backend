package com.backendSupermercado.supermercasdo.mapper.roles;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;

public class RolMapper {
    
    public static RolDto toDto(Rol rol) {

        if (rol == null) return null;

        RolDto dto = new RolDto();

        dto.setIdRol(rol.getIdRol());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        dto.setEstado(rol.getEstado());

        // ⚠️ cuidado con LAZY
        dto.setCantidadUsuarios(
                rol.getUsuarioRoles() != null
                        ? rol.getUsuarioRoles().size()
                        : 0
        );

        return dto;
    }
}
