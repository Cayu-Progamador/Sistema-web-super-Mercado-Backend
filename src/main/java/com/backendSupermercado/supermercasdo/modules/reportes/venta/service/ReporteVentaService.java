package com.backendSupermercado.supermercasdo.modules.reportes.venta.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaDto;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaFiltro;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.model.TipoReporteVenta;

public interface ReporteVentaService {
    Page<ReporteVentaDto.General> general(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.Ganancia> ganancias(ReporteVentaFiltro filtro, Pageable pageable);
    List<ReporteVentaDto.DetalleGanancia> detalleGanancia(Long idVenta);
    Page<ReporteVentaDto.ProductoVendido> productosMasVendidos(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.ProductoVendido> productosMenosVendidos(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.ProductoSinVenta> productosSinVentas(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.PorMetodoPago> porMetodoPago(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.PorCajero> porCajero(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.ContadoCredito> contadoCredito(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.PorPeriodo> porPeriodo(ReporteVentaFiltro filtro, Pageable pageable);
    Page<ReporteVentaDto.PorCliente> porCliente(ReporteVentaFiltro filtro, Pageable pageable);
    ReporteVentaExportacion prepararExportacion(TipoReporteVenta tipo, ReporteVentaFiltro filtro);
}
