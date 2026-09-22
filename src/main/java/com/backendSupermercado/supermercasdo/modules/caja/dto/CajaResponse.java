package com.backendSupermercado.supermercasdo.modules.caja.dto;

import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoCaja;

public record CajaResponse(Long idCaja, String nombre, EstadoCaja estado, boolean ocupada) {
}
