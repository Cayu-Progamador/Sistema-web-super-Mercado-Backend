package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfiguracionCorreoResponseDto {
    private String nombreUsuario;
    private String correoUsuario;
    private Boolean passwordConfigurada;
    private Boolean activo;
}
