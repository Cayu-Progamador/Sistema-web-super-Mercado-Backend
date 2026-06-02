package com.backendSupermercado.supermercasdo.modules.usuario.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;

public interface UsuarioService{
    //ver perfil del usuario logueado
    public UsuarioPerfilDto getMyProfile();

    //cambiar contrasena del usuario logueado
    public void changePassword(CambiarPasswordrequestDto request);
    
    //listar usuarios
    Page<UsuarioListadoResponseDto> listarUsuarios(Pageable pageable);

    
}
