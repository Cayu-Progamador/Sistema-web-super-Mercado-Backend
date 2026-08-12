package com.backendSupermercado.supermercasdo.modules.proveedor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    ProveedorResponseDto activarProveedor(Long id);

    ProveedorResponseDto desactivarProveedor(Long id);

    List<ProveedorListadoDto> exportarProveedores(String busqueda, String tipo, Boolean estado, Long idCiudad);

    byte[] exportarProveedoresPDF(String busqueda, String tipo, Boolean estado, Long idCiudad, String username);

    byte[] exportarProveedoresExcel(String busqueda, String tipo, Boolean estado, Long idCiudad);

    byte[] exportarProveedorDetallePDF(Long id, String username);

    byte[] exportarProveedorDetalleExcel(Long id);
}
