package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardVentasDto(
        BigDecimal ventasHoy,
        long cantidadVentasHoy,
        BigDecimal ventasSemana,
        List<VentaPorDiaDto> ventasPorDia,
        List<VentaPorMetodoPagoDto> ventasPorMetodoPago,
        List<ProductoMasVendidoDto> productosMasVendidos) {

    public record VentaPorDiaDto(String fecha, String etiqueta, BigDecimal total, long cantidadVentas) {}
    public record VentaPorMetodoPagoDto(String metodo, BigDecimal total) {}
    public record ProductoMasVendidoDto(String producto, BigDecimal cantidad) {}
}
