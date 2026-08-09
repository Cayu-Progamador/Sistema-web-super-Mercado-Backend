package com.backendSupermercado.supermercasdo.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorEstadisticasDto {
    private long total;
    private long empresas;
    private long personas;
    private long activos;
    private long inactivos;
}
