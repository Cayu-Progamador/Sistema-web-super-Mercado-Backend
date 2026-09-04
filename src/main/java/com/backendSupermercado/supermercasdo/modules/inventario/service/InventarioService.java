package com.backendSupermercado.supermercasdo.modules.inventario.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.ExistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.InventarioResumenDto;

public interface InventarioService {
    Page<ExistenciaResponseDto> listarExistencias(String buscar, String almacen, String ubicacion,
            String categoria, String estado, String lote, java.time.LocalDate vencimientoHasta, Pageable pageable);
    ExistenciaResponseDto obtenerDetalle(Long id);
    InventarioResumenDto obtenerResumen();
    byte[] exportarExistenciasPdf(String buscar, String almacen, String ubicacion,
            String categoria, String estado, String lote, java.time.LocalDate vencimientoHasta);
}
