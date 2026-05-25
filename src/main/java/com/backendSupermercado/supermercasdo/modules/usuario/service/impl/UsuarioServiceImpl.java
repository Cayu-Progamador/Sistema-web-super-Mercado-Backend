package com.backendSupermercado.supermercasdo.modules.usuario.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.mapper.usuario.UsuarioMapper;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    //ver perfil del usuario logueado
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
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

    
}
