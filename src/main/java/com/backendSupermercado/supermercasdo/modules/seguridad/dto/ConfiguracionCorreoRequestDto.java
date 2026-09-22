package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracionCorreoRequestDto {
    private String passwordEmail;
    private Boolean activo;
}
