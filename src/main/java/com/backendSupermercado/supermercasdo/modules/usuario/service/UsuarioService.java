package com.backendSupermercado.supermercasdo.modules.usuario.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.DashboardUsuarioDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioDetalleDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioFiltrosDto;
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
    public void actualizarUsuario(Long id, UsuarioUpdateDto dto, Long idUsuarioLogueado);

    //obtener estadisticas del usuario
    public DashboardUsuarioDto obtenerEstadisticasUsuario();

    //buscar usuario por nombre
    public List<UsuarioListadoResponseDto> buscarPorUsername(String username);

    Page<UsuarioListadoResponseDto> buscarPorUsernamePaginado(String username, Pageable pageable);
 
    //filtrar usuarios con especificaciones
    Page<UsuarioListadoResponseDto> filtrarUsuarios(UsuarioFiltrosDto filtros, Pageable pageable);

    //detalle de usuario
    public UsuarioDetalleDto obtenerDetalleUsuario(Long id);

    //exportar detalle de usuario a PDF
    byte[] exportarUsuarioDetallePDF(Long id, String username);

    //exportar usuarios (sin paginación)
    List<UsuarioListadoResponseDto> exportarUsuarios(UsuarioFiltrosDto filtros);

    //exportar usuarios a PDF
    byte[] exportarUsuariosPDF(UsuarioFiltrosDto filtros, String username);

    //exportar usuarios a Excel
    byte[] exportarUsuariosExcel(UsuarioFiltrosDto filtros);
}
