package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContratoDashboardDto {
    private long total;
    private long activos;
    private long proximosAVencer;
    private long vencidos;
    private long suspendidos;
    private long finalizados;
}
