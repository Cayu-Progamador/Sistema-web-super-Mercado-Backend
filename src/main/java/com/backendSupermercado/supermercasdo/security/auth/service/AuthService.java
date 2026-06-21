package com.backendSupermercado.supermercasdo.security.auth.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ResetPasswordRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.PasswordResetToken;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.PasswordResetTokenRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.EmailService;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.SeguridadUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.LoginUsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.SeguridadUsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.security.auth.dto.RegistroRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.UsuarioResponseDto;
import com.backendSupermercado.supermercasdo.security.jwt.JwtUtil;
import com.backendSupermercado.supermercasdo.shared.util.FechaUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        
        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
        private final RolRepository rolRepository;
        private final EmpleadoRepository empleadoRepository;
        private final PasswordResetTokenRepository passwordResetTokenRepository;
        private final JwtUtil jwtUtil;
        private final LoginUsuarioRepository loginUsuarioRepository;
        private final EmailService emailService;
        private final SeguridadUsuarioRepository seguridadUsuarioRepository;

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
                                .orElseThrow(() -> new ResourceConflictException(
                                                "Empleado no encontrado"));

                // CREAR USUARIO
                Usuario usuario = new Usuario();

                usuario.setUsername(request.getUsername());

                usuario.setPassword(passwordEncoder.encode(request.getPassword()));
                usuario.setEmpleado(empleado);
                // fecha de creacion
                usuario.setFechaCreacion(FechaUtil.ahora());
                usuario.setActivo(true);

                // asignar roles
                List<Rol> roles = asignarRolesDesdeRequest(request.getRoles());

                for (Rol rol : roles) {
                        UsuarioRol usuarioRol = new UsuarioRol();
                        usuarioRol.setUsuario(usuario);
                        usuarioRol.setRol(rol);
                        // fecha de asignacion
                        usuarioRol.setFechaAsignacion(FechaUtil.ahora());
                        usuario.getUsuarioRoles().add(usuarioRol);
                }

                Usuario usuarioGuardado = usuarioRepository.save(usuario);

                // Crear configuración de seguridad
                SeguridadUsuario seguridad = new SeguridadUsuario();
                seguridad.setUsuario(usuarioGuardado);
                seguridad.setIntentoFallidos(0);
                seguridad.setBloquedaHasta(null);
                seguridadUsuarioRepository.save(seguridad);

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
                                        .orElseThrow(() -> new ResourceConflictException(
                                                        "Rol ROLE_USER no encontrado"));

                        return List.of(rolDefault);
                }
                // roles enviados
                return roleNames.stream()
                                .map(role -> rolRepository.findByNombre(role)
                                                .orElseThrow(() -> new ResourceConflictException(
                                                                "Rol no encontrado: " + role)))
                                .toList();
        }

        // LOGIN USUARIO
        // LOGIN USUARIO
        public String login(String username, String password, String ip) {

                // 1. Buscar Usuario y Seguridad
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));

                
                                //validar si es asta activo
                if(!Boolean.TRUE.equals(usuario.getActivo())) {
                        throw new BadCredentialsException("Su cuenta se encuentra inactiva.");

                }
//busar seguridad del usuario
                SeguridadUsuario seguridad = seguridadUsuarioRepository.findByUsuario(usuario)
                                .orElseThrow(() -> new ResourceConflictException(
                                                "Configuración de seguridad no encontrada"));

                // Capturar el tiempo exacto de la petición una sola vez
                LocalDateTime ahora = LocalDateTime.now();

                // 2. Si expiró el bloqueo, limpiar el estado
                if (seguridad.getBloquedaHasta() != null && !ahora.isBefore(seguridad.getBloquedaHasta())) {
                        seguridad.setBloquedaHasta(null);
                        seguridad.setIntentoFallidos(0);
                        seguridadUsuarioRepository.save(seguridad);
                }

                // 3. Validar si actualmente está bloqueado
                if (seguridad.getBloquedaHasta() != null) {
                        long segundos = ChronoUnit.SECONDS.between(ahora, seguridad.getBloquedaHasta());
                        throw new BadCredentialsException(
                                        "Usuario bloqueado. Intente nuevamente en " + segundos + " segundos");
                }

                // 4. Validar si el usuario está activo
                if (!usuario.getActivo()) {
                        throw new BadCredentialsException("Usuario no activo");
                }

                // 5. VALIDAR PASSWORD
                if (!passwordEncoder.matches(password, usuario.getPassword())) {

                        int intentos = seguridad.getIntentoFallidos() + 1;
                        seguridad.setIntentoFallidos(intentos);

                        // Si llega a 3 intentos, bloquear por 30 segundos
                        if (intentos >= 3) {
                                seguridad.setBloquedaHasta(ahora.plusSeconds(30));
                                seguridadUsuarioRepository.save(seguridad);

                                throw new BadCredentialsException(
                                                "Usuario bloqueado por 30 segundos debido a demasiados intentos fallidos.");
                        }

                        // Si no llega a 3, solo guardar el intento fallido
                        seguridadUsuarioRepository.save(seguridad);
                        throw new BadCredentialsException("Contraseña incorrecta.");
                }

                // 6. LOGIN CORRECTO: Limpiar historial de fallos
                seguridad.setIntentoFallidos(0);
                seguridad.setBloquedaHasta(null);
                seguridadUsuarioRepository.save(seguridad);

                // Guardar historial de login
                LoginUsuario loginUsuario = new LoginUsuario();
                loginUsuario.setFechaLogin(FechaUtil.ahora());
                loginUsuario.setIp(ip);
                loginUsuario.setUsuario(usuario);
                loginUsuarioRepository.save(loginUsuario);

                // GENERAR Y RETORNAR TOKEN
                return jwtUtil.generateToken(usuario.getUsername());
        }

        
        // recuperar contrasena mediante email
        @Transactional
        public void forgotPassword(String email) {

                if (email == null || email.isBlank()) {
                        throw new BadCredentialsException("El email es obligatorio");
                }

                Usuario usuario = usuarioRepository.findByCorreo(email)
                                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));

                // eliminar PINs anteriores del usuario
                passwordResetTokenRepository.deleteByUsuario(usuario);

                // generar PIN
                String pin = generarPin6Digitos();

                PasswordResetToken reset = new PasswordResetToken();
                reset.setPin(pin);
                reset.setUsuario(usuario);
                reset.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
                reset.setUsed(false);
                reset.setAttempts(0);
                reset.setCreatedAt(LocalDateTime.now());

                passwordResetTokenRepository.save(reset);

                emailService.sendRecoverEmail(email, pin);
        }

        // generar PIN de 6 dígitos
        private String generarPin6Digitos() {
                int pin = 100000 + new Random().nextInt(900000);
                return String.valueOf(pin);
        }

        // verificar el pin
        @Transactional
        public void verifyPin(String email, String pinIngresado) {

                Usuario usuario = usuarioRepository.findByCorreo(email)
                                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));

                PasswordResetToken token = passwordResetTokenRepository
                                .findTopByUsuarioOrderByCreatedAtDesc(usuario)
                                .orElseThrow(() -> new ResourceConflictException("PIN no encontrado"));

                // verificar expiración
                if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
                        throw new BadCredentialsException("PIN expirado");
                }

                // bloquear intentos
                if (token.getAttempts() >= 3) {
                        throw new BadCredentialsException("PIN bloqueado");
                }

                // validar PIN
                if (!token.getPin().equals(pinIngresado)) {

                        token.setAttempts(token.getAttempts() + 1);
                        passwordResetTokenRepository.save(token);

                        throw new BadCredentialsException("PIN incorrecto");
                }

                // marcar usado
                token.setUsed(true);
                passwordResetTokenRepository.save(token);
        }

        // resetear contraseña
        @Transactional
        public void resetPassword(ResetPasswordRequestDto request) {

                if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
                        throw new BadCredentialsException("La nueva contraseña no puede estar vacía");
                }

                Usuario usuario = usuarioRepository.findByCorreo(request.getEmail())
                                .orElseThrow(() -> new ResourceConflictException("Usuario no encontrado"));

                usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
                usuarioRepository.save(usuario);
        }

}
