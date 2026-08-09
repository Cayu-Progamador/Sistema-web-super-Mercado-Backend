package com.backendSupermercado.supermercasdo.modules.proveedor.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequestDto {

    @NotBlank(message = "El tipo de proveedor es obligatorio")
    @Pattern(regexp = "^(EMPRESA|PERSONA)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "El tipo de proveedor debe ser EMPRESA o PERSONA")
    private String tipo;

    private EmpresaRequestDto empresa;

    private PersonaRequestDto persona;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpresaRequestDto {

        @NotBlank(message = "El NIT es obligatorio")
        @Size(max = 20, message = "El NIT no puede superar 20 caracteres")
        private String nit;

        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 255, message = "La razón social no puede superar 255 caracteres")
        private String razonSocial;

        @Size(max = 255, message = "El nombre comercial no puede superar 255 caracteres")
        private String nombreComercial;

        @Size(max = 255, message = "El representante legal no puede superar 255 caracteres")
        private String representanteLegal;

        private List<ContactoRequestDto> contactos;

        private DireccionRequestDto direccion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonaRequestDto {

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 255, message = "Los nombres no pueden superar 255 caracteres")
        private String nombres;

        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 255, message = "El apellido paterno no puede superar 255 caracteres")
        private String apellidoPaterno;

        @Size(max = 255, message = "El apellido materno no puede superar 255 caracteres")
        private String apellidoMaterno;

        @NotBlank(message = "El CI es obligatorio")
        @Size(max = 20, message = "El CI no puede superar 20 caracteres")
        private String ci;

        @NotBlank(message = "La fecha de nacimiento es obligatoria")
        private String fechaNacimiento;

        @NotBlank(message = "El sexo es obligatorio")
        @Size(max = 20, message = "El sexo no puede superar 20 caracteres")
        private String sexo;

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        private String telefono;

        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        private String correo;

        private DireccionRequestDto direccion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactoRequestDto {

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        private String telefono;

        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        private String correo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DireccionRequestDto {

        @Size(max = 255, message = "La zona no puede superar 255 caracteres")
        private String zona;

        @Size(max = 255, message = "La calle no puede superar 255 caracteres")
        private String calle;

        @Size(max = 20, message = "El número no puede superar 20 caracteres")
        private String numero;

        @Size(max = 255, message = "La referencia no puede superar 255 caracteres")
        private String referencia;

        @Size(max = 255, message = "La ciudad no puede superar 255 caracteres")
        private String ciudad;
    }
}
