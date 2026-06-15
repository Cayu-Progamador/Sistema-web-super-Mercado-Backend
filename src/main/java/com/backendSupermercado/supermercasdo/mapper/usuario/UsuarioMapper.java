package com.backendSupermercado.supermercasdo.mapper.usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioDetalleDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

@Component
public class UsuarioMapper {

        // maper de usuario a dto
        public UsuarioPerfilDto toDto(Usuario usuario) {
                UsuarioPerfilDto dto = new UsuarioPerfilDto();

                dto.setUsername(usuario.getUsername());

                // Empleado (evitar null)
                if (usuario.getEmpleado() != null) {
                        dto.setNombreEmpleado(usuario.getEmpleado().getNombre());
                        // dto.setApellidoEmpleado(usuario.getEmpleado().getApellido());
                }

                // Roles
                dto.setRoles(
                                usuario.getUsuarioRoles()
                                                .stream()
                                                .map(ur -> ur.getRol().getNombre())
                                                .collect(Collectors.toList()));

                return dto;
        }

        // mapper de usuario a listado de usuarios
        public UsuarioListadoResponseDto toListadoResponse(Usuario usuario) {

                String nombreCompleto = "";

                if (usuario.getEmpleado() != null) {
                        nombreCompleto = usuario.getEmpleado().getNombre();

                }

                String correo = usuario.getEmpleado() != null
                                ? usuario.getEmpleado().getEmail()
                                : "";

                String rol = usuario.getUsuarioRoles().isEmpty()
                                ? ""
                                : usuario.getUsuarioRoles()
                                                .get(0)
                                                .getRol()
                                                .getNombre();

                LocalDateTime ultimoAcceso = usuario.getLoginUsuarios()
                                .stream()
                                .map(LoginUsuario::getFechaLogin)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);

                Long empleadoId = usuario.getEmpleado() != null
                                ? usuario.getEmpleado().getIdEmpleado()
                                : null;

                return new UsuarioListadoResponseDto(
                                usuario.getIdUsuario(),
                                nombreCompleto,
                                usuario.getUsername(),
                                correo,
                                rol,
                                usuario.getActivo(),
                                ultimoAcceso,
                                empleadoId);
        }

        public UsuarioListadoResponseDto listadoDtoBuscar(Usuario usuario) {
                return new UsuarioListadoResponseDto(
                                usuario.getIdUsuario(),
                                usuario.getEmpleado().getNombre(),
                                usuario.getUsername(),
                                usuario.getEmpleado().getEmail(),
                                usuario.getUsuarioRoles()
                                                .stream()
                                                .findFirst()
                                                .map(ur -> ur.getRol().getNombre())
                                                .orElse("Sin rol"),
                                usuario.getActivo(),
                                usuario.getLoginUsuarios()
                                                .stream()
                                                .findFirst()
                                                .map(LoginUsuario::getFechaLogin)
                                                .orElse(null),
                                usuario.getEmpleado().getIdEmpleado());
        }

        // detalle de usuario
        public UsuarioDetalleDto toDetalleDto(Usuario usuario) {

                List<String> roles = usuario.getUsuarioRoles()
                                .stream()
                                .map(ur -> ur.getRol().getNombre())
                                .toList();

                LocalDateTime ultimoAcceso = usuario.getLoginUsuarios()
                                .stream()
                                .map(LoginUsuario::getFechaLogin)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);

                return new UsuarioDetalleDto(
                                usuario.getUsername(),
                                usuario.getActivo(),
                                usuario.getFechaCreacion(),
                                ultimoAcceso,
                                usuario.getEmpleado().getNombre(),
                                usuario.getEmpleado().getEmail(),
                                roles);
        }

}
