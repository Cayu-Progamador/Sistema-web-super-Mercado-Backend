package com.backendSupermercado.supermercasdo.modules.configuracion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracionEmpresaRequestDto {
    private String nombreComercial;
    private String razonSocial;
    private String nit;
    private String telefono;
    private String correo;
    private String departamento;
    private String direccion;
    private String mensajeComprobante;
}
