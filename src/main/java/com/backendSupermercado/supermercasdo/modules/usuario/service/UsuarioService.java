package com.backendSupermercado.supermercasdo.modules.usuario.service;

import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;

public interface UsuarioService{
    //ver perfil del usuario logueado
    public UsuarioPerfilDto getMyProfile();
    
}
