package com.backendSupermercado.supermercasdo.modules.usuario.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.usuario.UsuarioMapper;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.DashboardUsuarioDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioDetalleDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioUpdateDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.AuditoriaUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.AuditoriaUsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRolRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;
import com.backendSupermercado.supermercasdo.shared.util.FechaUtil;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // ver perfil del usuario logueado
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaUsuarioRepository aud;
    @Autowired

    private UsuarioMapper usuarioMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRolRepository usuarioRolRepository;

    // listar los usuarios logueados en el sistema para el perfil
    @Override
    @Transactional
    public UsuarioPerfilDto getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuarioMapper.toDto(usuario);
    }

    // cambiar contrasena del usuario logueado
    @Override
    @Transactional
    public void changePassword(CambiarPasswordrequestDto request) {
        Usuario usuario = obtenerUsuarioLogueado();

        // verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new ResourceConflictException("La contraseña actual no es correcta");
        }

        // confirmar constrasena nueva
        if (!request.getPasswordNueva().equals(request.getConfirmarPassword())) {
            throw new ResourceConflictException("Las contraseñas no coinciden");
        }

        // evitar reutilizar la misma contraseña
        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            throw new ResourceConflictException("La contraseña no puede ser la misma que la actual");
        }

        // cambiar la contraseña
        usuario.setPassword(
                passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
        // auditoria de cambio de contrasena
        registrarAuditoriaCambioPassword(usuario);
    }

    // registrar auditoria de cambio de contrasena
    private void registrarAuditoriaCambioPassword(Usuario usuario) {
        AuditoriaUsuario auditoria = AuditoriaUsuario.builder()
                .accion("CAMBIO_CONTRASENA")
                .descripcion("El Usuario cambio su contrasena")
                .fecha(FechaUtil.ahora())
                .usuario(usuario)
                .creadoPor(usuario.getIdUsuario())
                .build();
        aud.save(auditoria);
    }

    // metodo para obtener el usuario logueado
    private Usuario obtenerUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));
    }

    // listar usuarios
    @Override
    public Page<UsuarioListadoResponseDto> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toListadoResponse);
    }

    // desactivar el usuario borrado logico
    @Transactional
    public void desactivarUsuario(Long id) {

        // validar que el id no sea nulo
        if (id == null) {
            throw new ResourceConflictException("El usuario no puede ser nulo");
        }
        // buscar el usuario
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));

        // validar estado actual
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResourceConflictException("El usuario ya esta inactivo");
        }
        // desactivar el usuario
        usuario.setActivo(false);

        // guardar los cambios
        usuarioRepository.save(usuario);
    }

    // activar el usuario
    @Transactional
    public void activarUsuario(Long id) {

        if (id == null) {
            throw new ResourceConflictException("El id no puede ser nulo");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado con id: " + id));

        // ya está activo
        if (Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResourceConflictException("El usuario ya está activo");
        }

        usuario.setActivo(true);

        usuarioRepository.save(usuario);
    }

    // editar un usuario
    @Transactional
    public void actualizarUsuario(
            Long id,
            UsuarioUpdateDto dto,
            Long idUsuarioLogueado) {

        Usuario usuario = obtenerUsuario(id);

        StringBuilder cambios = new StringBuilder();

        actualizarUsername(usuario, dto, cambios);
        actualizarPassword(usuario, dto, cambios);
        actualizarEmpleado(usuario, dto, cambios);
        actualizarRoles(usuario, dto, cambios);

        if (cambios.length() == 0) {
            return;
        }

        usuarioRepository.save(usuario);

        registrarAuditoria(usuario, cambios, idUsuarioLogueado);
    }

    // obtener usuario
    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));
    }

    // username
    private void actualizarUsername(
            Usuario usuario,
            UsuarioUpdateDto dto,
            StringBuilder cambios) {

        if (dto.getUsername() == null ||
                dto.getUsername().equals(usuario.getUsername())) {
            return;
        }

        boolean existe = usuarioRepository.existsByUsername(dto.getUsername());

        if (existe) {
            throw new ResourceConflictException(
                    "El nombre de usuario ya existe");
        }

        cambios.append("Username: ")
                .append(usuario.getUsername())
                .append(" -> ")
                .append(dto.getUsername())
                .append("; ");

        usuario.setUsername(dto.getUsername());
    }

    // password (opcional)
    private void actualizarPassword(
            Usuario usuario,
            UsuarioUpdateDto dto,
            StringBuilder cambios) {

        if (dto.getPassword() == null ||
                dto.getPassword().isBlank()) {
            return;
        }

        usuario.setPassword(
                passwordEncoder.encode(dto.getPassword()));

        cambios.append("Password actualizada; ");
    }

    // empleado
    private void actualizarEmpleado(
            Usuario usuario,
            UsuarioUpdateDto dto,
            StringBuilder cambios) {

        if (dto.getEmpleadoId() == null) {
            return;
        }

        Long empleadoActualId = usuario.getEmpleado() != null
                ? usuario.getEmpleado().getIdEmpleado()
                : null;

        if (dto.getEmpleadoId().equals(empleadoActualId)) {
            return;
        }

        Usuario usuarioExistente = usuarioRepository
                .findByEmpleadoIdEmpleado(dto.getEmpleadoId())
                .orElse(null);

        if (usuarioExistente != null &&
                !usuarioExistente.getIdUsuario().equals(usuario.getIdUsuario())) {

            throw new ResourceConflictException(
                    "El empleado ya tiene usuario");
        }

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new ResourceConflictException("Empleado no encontrado"));

        cambios.append("Empleado actualizado; ");

        usuario.setEmpleado(empleado);
    }

    // roles
    private void actualizarRoles(
            Usuario usuario,
            UsuarioUpdateDto dto,
            StringBuilder cambios) {

        if (dto.getRoles() == null) {
            return;
        }

        Set<String> rolesActuales = usuario.getUsuarioRoles()
                .stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.toSet());

        Set<String> rolesNuevos = new HashSet<>(dto.getRoles());

        if (rolesActuales.equals(rolesNuevos)) {
            return;
        }

        List<Rol> roles = rolRepository.findByNombreIn(dto.getRoles());

        if (roles.size() != dto.getRoles().size()) {
            throw new ResourceConflictException(
                    "Uno o más roles no existen");
        }

        usuario.getUsuarioRoles().clear();

        for (Rol rol : roles) {

            UsuarioRol usuarioRol = new UsuarioRol();

            usuarioRol.setUsuario(usuario);
            usuarioRol.setRol(rol);
            usuarioRol.setEstado(true);
            usuarioRol.setFechaAsignacion(LocalDateTime.now());

            usuario.getUsuarioRoles().add(usuarioRol);
        }

        cambios.append("Roles actualizados; ");
    }

    // auditoría
    private void registrarAuditoria(
            Usuario usuario,
            StringBuilder cambios,
            Long idUsuarioLogueado) {

        AuditoriaUsuario auditoria = AuditoriaUsuario.builder()
                .accion("ACTUALIZAR_USUARIO")
                .descripcion(cambios.toString())
                .fecha(LocalDateTime.now())
                .creadoPor(idUsuarioLogueado)
                .usuario(usuario)
                .build();

        aud.save(auditoria);
    }

    //obtener estadisticas del usuario
    public DashboardUsuarioDto obtenerEstadisticasUsuario(){
        return new DashboardUsuarioDto(
                usuarioRepository.count(),
                usuarioRepository.countByActivoTrue(),
                usuarioRepository.countByActivoFalse(),
                usuarioRolRepository.contarAdministradores()
        );
    }

    //buscar usuario por nombre
    public List<UsuarioListadoResponseDto> buscarPorUsername(String username){
        return usuarioRepository.buscarPorUsername(username)
                .stream()
                .map(usuarioMapper::listadoDtoBuscar)
                .toList();
    }


    //detalle de usuario
    public UsuarioDetalleDto obtenerDetalleUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));
        return usuarioMapper.toDetalleDto(usuario);
    }
}