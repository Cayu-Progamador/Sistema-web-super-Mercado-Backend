package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RolEstadisticaDto {
    private long totalRoles;
    private long rolesActivos;
    private long rolesInactivos;
    private long totalPermisos;
}
