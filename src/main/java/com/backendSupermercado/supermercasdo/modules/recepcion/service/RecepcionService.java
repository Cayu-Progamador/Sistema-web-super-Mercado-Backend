package com.backendSupermercado.supermercasdo.modules.recepcion.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.recepcion.dto.CrearRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DevolucionRecepcionDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DistribuirRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.ProductoFaltanteDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionListadoDto;

public interface RecepcionService {

    RecepcionListadoDto crear(Long idUsuario, CrearRecepcionRequestDto dto);

    RecepcionListadoDto distribuir(Long idUsuario, Long idRecepcion, DistribuirRecepcionRequestDto dto);

    RecepcionListadoDto anular(Long idUsuario, Long idRecepcion);

    byte[] exportarPdf(Long id, String username);

    List<ProductoFaltanteDto> listarFaltantes();

    List<DevolucionRecepcionDto> listarDevoluciones();

    List<String> listarEstados();

    Page<RecepcionListadoDto> listar(String estado, String search, Long idProveedor,
        LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable);
 
    long contarPorEstado(String estado);
}