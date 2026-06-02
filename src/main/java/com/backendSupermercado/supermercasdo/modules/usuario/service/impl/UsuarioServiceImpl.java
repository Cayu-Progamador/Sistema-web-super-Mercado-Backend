package com.backendSupermercado.supermercasdo.modules.usuario.service.impl;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.usuario.UsuarioMapper;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.AuditoriaUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.AuditoriaUsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;
import com.backendSupermercado.supermercasdo.shared.util.FechaUtil;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final PasswordEncoder passwordEncoder;

    //ver perfil del usuario logueado
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private AuditoriaUsuarioRepository aud;
    @Autowired
    
    private UsuarioMapper usuarioMapper;
    
    UsuarioServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //listar los usuarios logueados en el sistema para el perfil
	@Override
    @Transactional
	public UsuarioPerfilDto getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsernameWithRoles(username)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
         
        return usuarioMapper.toDto(usuario);
	}

    //cambiar contrasena del usuario logueado
    @Override
    @Transactional
    public void changePassword(CambiarPasswordrequestDto request) {
        Usuario usuario = obtenerUsuarioLogueado();

        //verificar que la contraseña actual sea correcta
        if(!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())){
            throw new ResourceConflictException("La contraseña actual no es correcta");
        }

        //confirmar constrasena nueva
        if(!request.getPasswordNueva().equals(request.getConfirmarPassword())){
            throw new ResourceConflictException("Las contraseñas no coinciden");
        }

        //evitar reutilizar la misma contraseña
        if(passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            throw new ResourceConflictException("La contraseña no puede ser la misma que la actual");
        }

        //cambiar la contraseña
        usuario.setPassword(
            passwordEncoder.encode(request.getPasswordNueva())
        );
        usuarioRepository.save(usuario);
        //auditoria de cambio de contrasena
        registrarAuditoriaCambioPassword(usuario);
    }


    //registrar auditoria de cambio de contrasena
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

     //metodo para obtener el usuario logueado
    private Usuario obtenerUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
             .orElseThrow(()-> new ResourceConflictException("Usuario no encontrado"));
    }

    //listar usuarios
    @Override
    public Page<UsuarioListadoResponseDto> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toListadoResponse);
    }

    
}
