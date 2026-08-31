package com.backendSupermercado.supermercasdo.modules.almacen.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenListadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenCreadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.CrearAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ResponsableAlmacenDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ActualizarAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.TipoAlmacenDto;

public interface AlmacenService {
    Page<AlmacenListadoDto> listar(String search, String tipo, Boolean estado, Pageable pageable);
    List<TipoAlmacenDto> listarTiposActivos();
    List<ResponsableAlmacenDto> listarResponsablesActivos();
    AlmacenCreadoDto crear(CrearAlmacenRequestDto dto);
    AlmacenCreadoDto actualizar(Long idAlmacen, ActualizarAlmacenRequestDto dto);
    AlmacenCreadoDto activar(Long idAlmacen);
    AlmacenCreadoDto desactivar(Long idAlmacen);
    byte[] generarInventarioPdf(Long idAlmacen, String generadoPor);
}
