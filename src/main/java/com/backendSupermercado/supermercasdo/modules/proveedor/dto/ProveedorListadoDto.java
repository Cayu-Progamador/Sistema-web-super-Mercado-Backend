package com.backendSupermercado.supermercasdo.modules.proveedor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorListadoDto {
    private Long id;
    private String nombre;
    private String tipo;
    private String documento;
    private String contacto;
    private String telefono;
    private String correo;
    private String ciudad;
    private Boolean estado;
    private List<ContactoDto> contactos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoDto {
        private String telefono;
        private String correo;
    }
}
