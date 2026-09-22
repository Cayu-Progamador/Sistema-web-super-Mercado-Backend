package com.backendSupermercado.supermercasdo.modules.credito.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;

public record CreditoResponseDto(Long idCuenta, Long idVenta, String codigoVenta, String cliente, String celular,
        BigDecimal total, BigDecimal pagoInicial, BigDecimal saldo, LocalDate fechaLimite, EstadoCuenta estado,
        Integer cantidadProductos, List<CreditoProductoDto> productos, List<PagoCreditoResponseDto> pagos) {}
