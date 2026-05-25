package com.backendSupermercado.supermercasdo.security.auth.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.exceptions.ResourceNotFoundException;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.LoginUsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.security.auth.dto.RegistroRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.UsuarioResponseDto;
import com.backendSupermercado.supermercasdo.security.jwt.JwtUtil;
import com.backendSupermercado.supermercasdo.shared.util.FechaUtil;

@Service
public class AuthService {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private RolRepository rolRepository;

        @Autowired
        private EmpleadoRepository empleadoRepository;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private LoginUsuarioRepository loginUsuarioRepository;

        // REGISTRAR USUARIO
        public UsuarioResponseDto registrarUsuario(RegistroRequestDto request) {

                // VALIDAR USERNAME
                if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
                        throw new ResourceConflictException(
                                        "El username ya está registrado");
                }

                // VALIDAR EMPLEADO
                Long empleadoId = request.getEmpleadoId();

                if (usuarioRepository.findByEmpleadoIdEmpleado(empleadoId).isPresent()) {
                        throw new ResourceConflictException(
                                        "El empleado ya tiene usuario");
                }

                // BUSCAR EMPLEADO
                Empleado empleado = empleadoRepository.findById(empleadoId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Empleado no encontrado"));

                // CREAR USUARIO
                Usuario usuario = new Usuario();

                usuario.setUsername(request.getUsername());

                usuario.setPassword(passwordEncoder.encode(request.getPassword()));
                usuario.setEmpleado(empleado);
                //fecha de creacion
                usuario.setFechaCreacion(FechaUtil.ahora());
                usuario.setActivo(true);
                
                // asignar roles
                List<Rol> roles = asignarRolesDesdeRequest(request.getRoles());

                for (Rol rol : roles) {
                        UsuarioRol usuarioRol = new UsuarioRol();
                        usuarioRol.setUsuario(usuario);
                        usuarioRol.setRol(rol);
                        //fecha de asignacion
                        usuarioRol.setFechaAsignacion(FechaUtil.ahora());
                        usuario.getUsuarioRoles().add(usuarioRol);
                }

                Usuario usuarioGuardado = usuarioRepository.save(usuario);
                return new UsuarioResponseDto(
                                usuarioGuardado.getIdUsuario(),
                                usuarioGuardado.getUsername(),
                                usuarioGuardado.getActivo());
        }

        // ASIGNAR ROLES
        private List<Rol> asignarRolesDesdeRequest(
                        List<String> roleNames) {

                if (roleNames == null || roleNames.isEmpty()) {

                        Rol rolDefault = rolRepository.findByNombre("ROLE_USER")
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Rol ROLE_USER no encontrado"));

                        return List.of(rolDefault);
                }
                //roles enviados
                return roleNames.stream()
                                .map(role -> 
                                        rolRepository.findByNombre(role)
                                                .orElseThrow(() -> 
                                                        new ResourceNotFoundException(
                                                                "Rol no encontrado: " + role)))
                                .toList();
        }

        // LOGIN
        public String login(String username, String password, String ip) {

                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado"));

                //validar activo
                if(!usuario.getActivo()){
                        throw new BadCredentialsException(
                                        "Usuario no activo");
                }

                // VALIDAR PASSWORD
                if (!passwordEncoder.matches(password, usuario.getPassword())) {
                        throw new BadCredentialsException(
                                        "Contraseña incorrecta");
                }

                //guardar login
                LoginUsuario loginUsuario = new LoginUsuario();
                loginUsuario.setFechaLogin(FechaUtil.ahora());
                loginUsuario.setIp(ip);
                loginUsuario.setUsuario(usuario);
                loginUsuarioRepository.save(loginUsuario);
                
                // GENERAR TOKEN
                return jwtUtil.generateToken(usuario.getUsername());
        }

}
