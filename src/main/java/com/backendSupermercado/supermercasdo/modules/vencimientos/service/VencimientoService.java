package com.backendSupermercado.supermercasdo.modules.vencimientos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoFiltroDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResponseDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResumenDto;

public interface VencimientoService {

    Page<VencimientoResponseDto> listar(
            VencimientoFiltroDto filtros,
            Pageable pageable);

    VencimientoResumenDto obtenerResumen();

    byte[] exportarPdf(VencimientoFiltroDto filtros);
}
