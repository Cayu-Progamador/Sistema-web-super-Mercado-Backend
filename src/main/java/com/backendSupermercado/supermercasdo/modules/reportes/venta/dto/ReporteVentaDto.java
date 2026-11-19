package com.backendSupermercado.supermercasdo.modules.reportes.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ReporteVentaDto {

    private ReporteVentaDto() {
    }

    public record General(
            LocalDateTime fecha,
            String numeroVenta,
            String cliente,
            String cajero,
            String tipoVenta,
            String metodoPago,
            String estado,
            BigDecimal total) {
    }

    public record Ganancia(
            Long idVenta,
            String numeroVenta,
            LocalDateTime fecha,
            String cajero,
            Long cantidadProductos,
            BigDecimal totalVendido,
            BigDecimal costoTotal,
            BigDecimal ganancia,
            BigDecimal margenPorcentaje,
            String estadoCosto) {
    }

    public record DetalleGanancia(
            String codigo,
            String producto,
            BigDecimal cantidad,
            BigDecimal precioVentaUnitario,
            BigDecimal costoUnitario,
            BigDecimal totalVendido,
            BigDecimal costoTotal,
            BigDecimal ganancia) {
    }

    public record ProductoVendido(
            String codigo,
            String producto,
            String categoria,
            BigDecimal cantidadVendida,
            LocalDateTime ultimaVenta,
            BigDecimal importeGenerado) {
    }

    public record ProductoSinVenta(
            String codigo,
            String producto,
            String categoria,
            BigDecimal stockActual,
            LocalDateTime ultimaVenta,
            Long diasSinVenta) {
    }

    public record PorMetodoPago(
            String metodoPago,
            Long cantidadOperaciones,
            BigDecimal participacionPorcentaje,
            BigDecimal totalRecibido) {
    }

    public record PorCajero(
            Long cajeroId,
            String cajero,
            String caja,
            Long cantidadVentas,
            BigDecimal ticketPromedio,
            BigDecimal totalVendido) {
    }

    public record ContadoCredito(
            String tipoVenta,
            Long cantidadOperaciones,
            BigDecimal importeTotal,
            BigDecimal montoCobrado,
            BigDecimal saldoPendiente) {
    }

    public record PorPeriodo(
            LocalDate periodo,
            Long cantidadVentas,
            BigDecimal ventasContado,
            BigDecimal ventasCredito,
            BigDecimal totalVendido,
            BigDecimal ticketPromedio) {
    }

    public record PorCliente(
            Long clienteId,
            String documento,
            String cliente,
            Long cantidadCompras,
            LocalDateTime ultimaCompra,
            BigDecimal totalContado,
            BigDecimal totalCredito,
            BigDecimal totalGastado) {
    }
}
