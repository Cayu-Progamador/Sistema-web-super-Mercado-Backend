package com.backendSupermercado.supermercasdo.mapper.usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.contrato.repository.ContratoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.service.FotoPerfilService;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioDetalleDto;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.LoginUsuarioRepository;

import lombok.RequiredArgsConstructor;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

        private final FotoPerfilService fotoPerfilService;
        private final ContratoRepository contratoRepository;
        private final LoginUsuarioRepository loginUsuarioRepository;

        // maper de usuario a dto
        public UsuarioPerfilDto toDto(Usuario usuario) {
                UsuarioPerfilDto dto = new UsuarioPerfilDto();

                dto.setUsername(usuario.getUsername());

                // Empleado (evitar null)
                if (usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null) {
                        var persona = usuario.getEmpleado().getPersona();
                        String nombreCompleto = persona.getNombres() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno();

                        dto.setNombreEmpleado(nombreCompleto.trim());
                        dto.setApellidoEmpleado(persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "");
                        dto.setFotoUrl(fotoPerfilService.obtenerUrlFoto(persona.getIdPersona()));
                        dto.setCi(persona.getCi() != null ? persona.getCi() : "");

                        if (persona.getSexo() != null) {
                                dto.setSexo(persona.getSexo().getNombre());
                        }

                        if (usuario.getEmpleado().getFechaContratacion() != null) {
                                dto.setFechaContratacion(usuario.getEmpleado().getFechaContratacion().toString());
                        }

                        var contacto = persona.getContactos() != null && !persona.getContactos().isEmpty()
                                        ? persona.getContactos().get(0)
                                        : null;
                        if (contacto != null) {
                                dto.setTelefono(contacto.getTelefono() != null ? contacto.getTelefono() : "");
                                dto.setCorreo(contacto.getCorreo() != null ? contacto.getCorreo() : "");
                        }

                        dto.setDireccion(armarDireccion(persona));

                        if (usuario.getEmpleado() != null) {
                                var contratoActivo = contratoRepository
                                                .findByEmpleadoAndEstado(usuario.getEmpleado(), "ACTIVO")
                                                .orElse(null);
                                if (contratoActivo != null && contratoActivo.getCargo() != null) {
                                        dto.setCargo(contratoActivo.getCargo().getNombre());
                                }
                        }
                }

                // Fecha de registro y último acceso
                if (usuario.getFechaCreacion() != null) {
                        dto.setFechaRegistro(usuario.getFechaCreacion());
                }

                dto.setUltimoAcceso(
                                usuario.getLoginUsuarios()
                                                .stream()
                                                .map(LoginUsuario::getFechaLogin)
                                                .max(LocalDateTime::compareTo)
                                                .orElse(null));

                // Estado de la cuenta
                dto.setActivo(usuario.getActivo());

                // Estadísticas de inicios de sesión
                LocalDateTime ahora = LocalDateTime.now();
                dto.setTotalIniciosSesion(loginUsuarioRepository.countByUsuario(usuario));
                dto.setIniciosUltimos7Dias(loginUsuarioRepository.countByUsuarioAndFechaLoginAfter(usuario, ahora.minusDays(7)));
                dto.setIniciosUltimos30Dias(loginUsuarioRepository.countByUsuarioAndFechaLoginAfter(usuario, ahora.minusDays(30)));

                // Roles
                dto.setRoles(
                                usuario.getUsuarioRoles()
                                                .stream()
                                                .map(ur -> ur.getRol().getNombre())
                                                .collect(Collectors.toList()));

                return dto;
        }

        private String armarDireccion(com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona persona) {
                var direccion = persona.getDireccion();
                if (direccion == null) {
                        return "";
                }

                StringBuilder sb = new StringBuilder();
                if (direccion.getCalle() != null && !direccion.getCalle().isBlank()) {
                        sb.append(direccion.getCalle());
                        if (direccion.getNumero() != null && !direccion.getNumero().isBlank()) {
                                sb.append(" ").append(direccion.getNumero());
                        }
                } else if (direccion.getZona() != null && !direccion.getZona().isBlank()) {
                        sb.append(direccion.getZona());
                }

                if (direccion.getCiudad() != null) {
                        if (sb.length() > 0) {
                                sb.append(", ");
                        }
                        sb.append(direccion.getCiudad().getNombre());
                }

                if (direccion.getZona() != null && !direccion.getZona().isBlank()
                                && !sb.toString().contains(direccion.getZona())) {
                        sb.append(" - ").append(direccion.getZona());
                }

                return sb.toString().trim();
        }

        // mapper de usuario a listado de usuarios
        public UsuarioListadoResponseDto toListadoResponse(Usuario usuario) {

                String nombreCompleto = "";

                if (usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null) {
                        var persona = usuario.getEmpleado().getPersona();
                        nombreCompleto = persona.getNombres() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno();
                }

                String correo = "";
                if(usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null){
                correo = usuario.getEmpleado()
                                .getPersona()
                                .getContactos()
                                .stream()
                                .findFirst()
                                .map(Contacto::getCorreo)
                                .orElse("");
                }
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
                String correo = usuario.getEmpleado()
                                .getPersona()
                                .getContactos()
                                .stream()
                                .findFirst()
                                .map(Contacto::getCorreo)
                                .orElse("");

                String nombreEmpleado = "";

                if(usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null){
                        var persona = usuario.getEmpleado().getPersona();
                        nombreEmpleado = persona.getNombres();
                }
                return new UsuarioListadoResponseDto(
                                usuario.getIdUsuario(),
                                nombreEmpleado,
                                usuario.getUsername(),
                                correo,
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

                String nombreCompleto = "";
                String ci = "";
                String correo = "";
                String telefono = "";

                if (usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null) {
                        var persona = usuario.getEmpleado().getPersona();
                        nombreCompleto = persona.getNombres() + " " 
                                + (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                                + " " + (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
                        ci = persona.getCi() != null ? persona.getCi() : "";

                        var contacto = persona.getContactos()
                                        .stream()
                                        .findFirst()
                                        .orElse(null);
                        if (contacto != null) {
                                correo = contacto.getCorreo() != null ? contacto.getCorreo() : "";
                                telefono = contacto.getTelefono() != null ? contacto.getTelefono() : "";
                        }
                }

                return new UsuarioDetalleDto(
                                usuario.getIdUsuario(),
                                usuario.getUsername(),
                                usuario.getActivo(),
                                usuario.getFechaCreacion(),
                                ultimoAcceso,
                                nombreCompleto.trim(),
                                ci,
                                correo,
                                telefono,
                                roles);
        }

}
