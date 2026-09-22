package com.backendSupermercado.supermercasdo.modules.credito.dto;

import java.math.BigDecimal;
public record CreditoResumenDto(BigDecimal totalPorCobrar, BigDecimal totalVencido, BigDecimal totalCobrado, long cuentasActivas) {}
