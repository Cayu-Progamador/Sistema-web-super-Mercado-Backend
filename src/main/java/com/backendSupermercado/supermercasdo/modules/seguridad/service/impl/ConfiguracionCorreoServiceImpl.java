package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoResponseDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionCorreo;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionSmtGlobal;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.ConfiguracionCorreoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.ConfiguracionSmtpGlobalRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.ConfiguracionCorreoService;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.MailPasswordCipher;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionCorreoServiceImpl implements ConfiguracionCorreoService {
    private final ConfiguracionCorreoRepository configuracionCorreoRepository;
    private final ConfiguracionSmtpGlobalRepository configuracionSmtpGlobalRepository;
    private final UsuarioRepository usuarioRepository;
    private final MailPasswordCipher mailPasswordCipher;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionCorreoResponseDto obtener(String username) {
        ConfiguracionCorreo configuracion = configuracionCorreoRepository.findFirstByActivoTrueOrderByIdAsc()
                .or(() -> configuracionCorreoRepository.findFirstByOrderByIdAsc())
                .orElse(null);
        Empleado empleado = configuracion != null ? configuracion.getEmpleado() : obtenerEmpleadoActual(username);
        return respuesta(empleado, configuracion);
    }

    @Override
    @Transactional
    public ConfiguracionCorreoResponseDto guardar(String username, ConfiguracionCorreoRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("La configuración es obligatoria");
        }

        ConfiguracionCorreo configuracion = configuracionCorreoRepository.findFirstByActivoTrueOrderByIdAsc()
                .or(() -> configuracionCorreoRepository.findFirstByOrderByIdAsc())
                .orElseGet(ConfiguracionCorreo::new);

        if (configuracion.getId() == null) {
            configuracion.setEmpleado(obtenerEmpleadoActual(username));
            configuracion.setSmtpConfig(obtenerOCrearSmtpGlobal());
        }

        String password = request.getPasswordEmail();
        if (password != null && !password.isBlank()) {
            configuracion.setPasswordEmail(mailPasswordCipher.encrypt(password.replaceAll("\\s+", "")));
        } else if (configuracion.getPasswordEmail() == null || configuracion.getPasswordEmail().isBlank()) {
            throw new IllegalArgumentException("La contraseña de aplicación de Gmail es obligatoria");
        }

        boolean activo = request.getActivo() == null || Boolean.TRUE.equals(request.getActivo());
        configuracion.setActivo(activo);

        if (activo) {
            configuracionCorreoRepository.findAll().stream()
                    .filter(otra -> otra.getId() != null && !otra.getId().equals(configuracion.getId()))
                    .forEach(otra -> otra.setActivo(false));
        }

        ConfiguracionCorreo guardada = configuracionCorreoRepository.save(configuracion);
        return respuesta(guardada.getEmpleado(), guardada);
    }

    private ConfiguracionSmtGlobal obtenerOCrearSmtpGlobal() {
        return configuracionSmtpGlobalRepository.findFirstByOrderByIdAsc().orElseGet(() -> {
            ConfiguracionSmtGlobal smtp = new ConfiguracionSmtGlobal();
            smtp.setSmtp("smtp.gmail.com");
            smtp.setPuerto(587);
            return configuracionSmtpGlobalRepository.save(smtp);
        });
    }

    private Empleado obtenerEmpleadoActual(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceConflictException("Usuario administrador no encontrado"));
        if (usuario.getEmpleado() == null) {
            throw new ResourceConflictException("El administrador no tiene un empleado asociado");
        }
        return usuario.getEmpleado();
    }

    private ConfiguracionCorreoResponseDto respuesta(Empleado empleado, ConfiguracionCorreo configuracion) {
        Persona persona = empleado.getPersona();
        String nombre = nombreCompleto(persona);
        String correo = persona.getContactos().stream()
                .map(Contacto::getCorreo)
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElseThrow(() -> new ResourceConflictException("El empleado remitente no tiene correo registrado"));
        boolean configurada = configuracion != null
                && configuracion.getPasswordEmail() != null
                && !configuracion.getPasswordEmail().isBlank();
        boolean activa = configuracion != null && Boolean.TRUE.equals(configuracion.getActivo());
        return new ConfiguracionCorreoResponseDto(nombre, correo, configurada, activa);
    }

    private String nombreCompleto(Persona persona) {
        return String.join(" ",
                valor(persona.getNombres()),
                valor(persona.getApellidoPaterno()),
                valor(persona.getApellidoMaterno()))
                .trim().replaceAll("\\s+", " ");
    }

    private String valor(String valor) {
        return valor == null ? "" : valor;
    }
}
