package com.backendSupermercado.supermercasdo.modules.empleado.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardEmpleadoDto {
    private Long totalEmpleados;
    private Long empleadosActivos;
    private Long empleadosInactivos;
    private Long cargos;
}
