package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.roles.RolMapper;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDetalleDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDetalleDto.RolDetallePermiso;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDetalleDto.RolDetalleUsuario;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolEstadisticaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.RolPermisos;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.PermisosRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService{

    private final RolRepository rolRepository;
    private final PermisosRepository permisosRepository;
    
    //listar roles en el nuevo usuario
    @Override
    public List<RolSelectDto> listarRolSeleccionado() {
        return rolRepository.listarRolSelect();
    }

    //tabla de roles en la tabla de roles para el crud (paginado)
    @Transactional(readOnly = true)
    public Page<RolDto> listarRoles(Pageable pageable) {
        return rolRepository.findAll(pageable)
                .map(RolMapper::toDto);
    }

    //crear un nuevo rol
    @Override
    public RolRespuestaDto crearRol(RolRequestDto dto) {
        //Validar duplicado (ignorando mayúsculas/minúsculas)
        String nombreRol = dto.getNombre().toUpperCase();
        if (rolRepository.existsByNombre(nombreRol)) {
            throw new ResourceConflictException("Ya existe un rol con ese nombre");
        }
        //  Crear entidad
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        rol.setDescripcion(dto.getDescripcion());
        rol.setEstado(true); // default activo

        Rol saved = rolRepository.save(rol);
       
        // 📤 Mapear a DTO de respuesta
        RolRespuestaDto response = new RolRespuestaDto();
        response.setIdRol(saved.getIdRol());
        response.setNombre(saved.getNombre());
        response.setDescripcion(saved.getDescripcion());
        response.setEstado(saved.getEstado());

        return response;

    }
    //buscar por nombre de usuario (paginado)
    @Override
    public Page<RolDto> buscarPorNombre(String nombre, Pageable pageable) {
        return rolRepository.buscarPorNombre(nombre, pageable)
                .map(RolMapper::toDto);
    }

    @Override
    public RolEstadisticaDto obtenerEstadisticas() {
        List<Rol> todos = rolRepository.findAll();
        long total = todos.size();
        long activos = todos.stream().filter(Rol::getEstado).count();
        long inactivos = total - activos;
        long permisos = permisosRepository.count();
        return new RolEstadisticaDto(total, activos, inactivos, permisos);
    }

    // Busca el rol por ID, si no existe lanza error
    @Override
    public void activarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Rol no encontrado"));
        // Si ya está activo, no hacer nada y notificar
        if (rol.getEstado()) {
            throw new ResourceConflictException("El rol ya está activo");
        }
        rol.setEstado(true);
        rolRepository.save(rol);
    }

    @Override
    public void desactivarRol(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Rol no encontrado"));
        // Si ya está inactivo, no hacer nada y notificar
        if (!rol.getEstado()) {
            throw new ResourceConflictException("El rol ya está inactivo");
        }
        rol.setEstado(false);
        rolRepository.save(rol);
    }

    // Actualiza nombre y descripción de un rol
    @Override
    public RolRespuestaDto actualizarRol(Long id, RolRequestDto dto) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Rol no encontrado"));
        // Validar duplicado si el nombre cambió
        String nuevoNombre = dto.getNombre().toUpperCase();
        if (!rol.getNombre().equals(nuevoNombre) && rolRepository.existsByNombre(nuevoNombre)) {
            throw new ResourceConflictException("Ya existe un rol con ese nombre");
        }
        rol.setNombre(nuevoNombre);
        rol.setDescripcion(dto.getDescripcion());
        Rol saved = rolRepository.save(rol);

        RolRespuestaDto response = new RolRespuestaDto();
        response.setIdRol(saved.getIdRol());
        response.setNombre(saved.getNombre());
        response.setDescripcion(saved.getDescripcion());
        response.setEstado(saved.getEstado());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RolDetalleDto obtenerDetalle(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Rol no encontrado"));

        List<RolDetallePermiso> permisos = new ArrayList<>();
        if (rol.getRolPermisos() != null) {
            for (RolPermisos rp : rol.getRolPermisos()) {
                if (rp.getPermiso() != null) {
                    permisos.add(new RolDetallePermiso(
                            rp.getPermiso().getIdPermiso(),
                            rp.getPermiso().getCodigo(),
                            rp.getPermiso().getNombre()
                    ));
                }
            }
        }

        List<RolDetalleUsuario> usuarios = new ArrayList<>();
        if (rol.getUsuarioRoles() != null) {
            for (UsuarioRol ur : rol.getUsuarioRoles()) {
                String nombreCompleto = "";
                String correo = "";
                if (ur.getUsuario().getEmpleado() != null
                        && ur.getUsuario().getEmpleado().getPersona() != null) {
                    var p = ur.getUsuario().getEmpleado().getPersona();
                    nombreCompleto = (p.getNombres() != null ? p.getNombres() : "")
                            + " " + (p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "")
                            + " " + (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
                    if (p.getContactos() != null && !p.getContactos().isEmpty()) {
                        correo = p.getContactos().get(0).getCorreo();
                    }
                }
                usuarios.add(new RolDetalleUsuario(
                        ur.getUsuario().getUsername(),
                        nombreCompleto.trim(),
                        correo,
                        ur.getEstado()
                ));
            }
        }

        int cantidad = usuarios.size();

        return new RolDetalleDto(
                rol.getIdRol(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getEstado(),
                cantidad,
                permisos,
                usuarios
        );
    }
}
