package com.backendSupermercado.supermercasdo.modules.proveedor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorEstadisticasDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorListadoDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorRequestDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorResponseDto;

public interface ProveedorService {

    ProveedorEstadisticasDto obtenerEstadisticas();

    ProveedorResponseDto obtenerProveedorPorId(Long id);

    Page<ProveedorListadoDto> listarProveedores(String busqueda, String tipo, Boolean estado, Long idCiudad, Pageable pageable);

    ProveedorResponseDto crearProveedor(ProveedorRequestDto dto);

    ProveedorResponseDto actualizarProveedor(Long id, ProveedorRequestDto dto);
}
