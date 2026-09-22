package com.backendSupermercado.supermercasdo.modules.configuracion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfiguracionEmpresaResponseDto {
    private Long id;
    private String nombreComercial;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String correo;
    private String departamento;
    private String direccion;
    private String logoUrl;
    private String fondoLoginUrl;
    private String mensajeComprobante;
}
