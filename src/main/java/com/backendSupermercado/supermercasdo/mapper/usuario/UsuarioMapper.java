package com.backendSupermercado.supermercasdo.mapper.usuario;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

@Component
public class UsuarioMapper {

    //maper de usuario a dto
    public UsuarioPerfilDto toDto(Usuario usuario) {
         UsuarioPerfilDto dto = new UsuarioPerfilDto();

        dto.setUsername(usuario.getUsername());

        // Empleado (evitar null)
        if (usuario.getEmpleado() != null) {
            dto.setNombreEmpleado(usuario.getEmpleado().getNombre());
            //dto.setApellidoEmpleado(usuario.getEmpleado().getApellido());
        }

        // Roles
        dto.setRoles(
                usuario.getUsuarioRoles()
                        .stream()
                        .map(ur -> ur.getRol().getNombre())
                        .collect(Collectors.toList())
        );

        return dto;
    }
    
}
