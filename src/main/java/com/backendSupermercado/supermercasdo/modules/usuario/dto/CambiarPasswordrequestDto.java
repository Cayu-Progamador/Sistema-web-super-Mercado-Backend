package com.backendSupermercado.supermercasdo.modules.usuario.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordrequestDto {
    private String passwordActual;
    private String passwordNueva;
    private String confirmarPassword;
}
