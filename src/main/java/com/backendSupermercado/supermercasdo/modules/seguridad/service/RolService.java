package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDetalleDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolEstadisticaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;

public interface RolService {
    
    //listar los roles selecciona en el usuario nuevo
    public List<RolSelectDto> listarRolSeleccionado();
    
    //tabla de roles en la tabla de roles para el crud (paginado)
    Page<RolDto> listarRoles(Pageable pageable);

    /// crear un nuevo rol

    public RolRespuestaDto crearRol(RolRequestDto dto);

    RolEstadisticaDto obtenerEstadisticas();

    void activarRol(Long id);

    void desactivarRol(Long id);

    Page<RolDto> buscarPorNombre(String nombre, Pageable pageable);

    RolRespuestaDto actualizarRol(Long id, RolRequestDto dto);

    RolDetalleDto obtenerDetalle(Long id);
}
