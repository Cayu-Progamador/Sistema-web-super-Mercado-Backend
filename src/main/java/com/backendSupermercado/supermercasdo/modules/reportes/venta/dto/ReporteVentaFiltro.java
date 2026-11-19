package com.backendSupermercado.supermercasdo.modules.reportes.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReporteVentaFiltro {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoVenta estadoVenta;
    private EstadoCuenta estadoCredito;
    private MetodoPago metodoPago;
    private String tipoVenta;
    private String agrupacion;
    private Long cajeroId;
    private Long clienteId;
    private Long cajaId;
    private Long productoId;
    private Long categoriaId;
    private Long marcaId;
    private BigDecimal margenMinimo;
    private BigDecimal margenMaximo;
}
