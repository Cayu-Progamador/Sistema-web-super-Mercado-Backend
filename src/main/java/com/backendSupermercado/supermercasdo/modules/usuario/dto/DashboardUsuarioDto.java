package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardUsuarioDto {
    private Long totalUsuarios;
    private Long usuariosActivos;
    private Long usuariosInactivos;
    private Long administradores;
}
