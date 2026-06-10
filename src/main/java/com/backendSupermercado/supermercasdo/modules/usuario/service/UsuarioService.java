package com.backendSupermercado.supermercasdo.modules.usuario.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioUpdateDto;

public interface UsuarioService{
    //ver perfil del usuario logueado
    public UsuarioPerfilDto getMyProfile();

    //cambiar contrasena del usuario logueado
    public void changePassword(CambiarPasswordrequestDto request);
    
    //listar usuarios
    Page<UsuarioListadoResponseDto> listarUsuarios(Pageable pageable);

    //desactivar el usuario borrado logico
    public void desactivarUsuario(Long id);

    //activar el usuario borrado logico
    public void activarUsuario(Long id);

    //editar un usuario
    public void actualizarUsuario(
            Long id,
            UsuarioUpdateDto dto,
            Long idUsuarioLogueado);
    
}
