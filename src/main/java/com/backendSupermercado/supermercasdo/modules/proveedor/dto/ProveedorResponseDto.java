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
public class ProveedorResponseDto {
    private Long id;
    private String tipo;
    private Boolean estado;
    private String fechaRegistro;

    private EmpresaResponseDto empresa;
    private PersonaResponseDto persona;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpresaResponseDto {
        private Long id;
        private String nit;
        private String razonSocial;
        private String nombreComercial;
        private String representanteLegal;
        private List<ContactoResponseDto> contactos;
        private DireccionResponseDto direccion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonaResponseDto {
        private Long id;
        private String nombreCompleto;
        private String nombres;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String ci;
        private String fechaNacimiento;
        private String sexo;
        private String telefono;
        private String correo;
        private DireccionResponseDto direccion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoResponseDto {
        private Long id;
        private String telefono;
        private String correo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionResponseDto {
        private Long id;
        private String zona;
        private String calle;
        private String numero;
        private String referencia;
        private String ciudad;
    }
}
