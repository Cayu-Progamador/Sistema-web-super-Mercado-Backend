package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.UbicacionListadoDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ActualizarUbicacionDto;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ProductoUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.CrearUbicacionDto;

public interface UbicacionAlmacenService {
    Page<UbicacionListadoDto> listar(String search, Long idAlmacen, String tipo, Boolean estado, Pageable pageable);
    UbicacionListadoDto actualizar(Long id, ActualizarUbicacionDto dto);
    UbicacionListadoDto activar(Long id);
    UbicacionListadoDto desactivar(Long id);
    List<ProductoUbicacionDto> listarProductos(Long id);
    UbicacionListadoDto crear(CrearUbicacionDto dto);
    byte[] generarPdf(Long id, String usuario);
}
