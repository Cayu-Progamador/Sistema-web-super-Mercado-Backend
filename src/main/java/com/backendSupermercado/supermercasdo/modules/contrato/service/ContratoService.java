package com.backendSupermercado.supermercasdo.modules.contrato.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetalleDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoListadoDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.RenovarContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoFiltrosDto;

public interface ContratoService {
    Page<ContratoListadoDto> listarContratos(
        String busqueda,
        String estado,
        Boolean controlaAsistencia,
        String tipoContrato,
        String tipoJornada,
        Long empleadoId,
        String fechaDesde,
        String fechaHasta,
        String fechaFinDesde,
        String fechaFinHasta,
        Pageable pageable
    );

    ContratoListadoDto crear(ContratoRequestDto dto);

    ContratoListadoDto actualizar(Long id, ContratoRequestDto dto);

    ContratoListadoDto activar(Long id);

    ContratoListadoDto desactivar(Long id);

    ContratoListadoDto finalizar(Long id, String motivoFin);

    ContratoListadoDto obtenerPorId(Long id);

    ContratoDetalleDto obtenerDetalle(Long id);

    ContratoListadoDto renovar(Long id, RenovarContratoRequestDto dto);

    byte[] exportarPdf(Long id, String username);

    ContratoDashboardDto obtenerDashboard();

    List<ContratoListadoDto> exportarContratos(ContratoFiltrosDto filtros);

    byte[] exportarContratosPDF(ContratoFiltrosDto filtros, String username);

    byte[] exportarContratosExcel(ContratoFiltrosDto filtros);
}
