package com.backendSupermercado.supermercasdo.modules.credito.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;

public record PagoCreditoResponseDto(Long id, BigDecimal monto, BigDecimal montoEfectivo, BigDecimal montoQr, MetodoPago metodo, String referenciaQr, LocalDateTime fechaPago) {}
