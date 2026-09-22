package com.backendSupermercado.supermercasdo.modules.credito.dto;

import java.math.BigDecimal;
public record CreditoProductoDto(String codigo, String producto, BigDecimal cantidad, BigDecimal importe) {}
