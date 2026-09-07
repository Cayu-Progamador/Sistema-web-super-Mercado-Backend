package com.backendSupermercado.supermercasdo.modules.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDto {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden superar 100 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 100, message = "El apellido paterno no puede superar 100 caracteres")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Size(max = 100, message = "El apellido materno no puede superar 100 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "El CI es obligatorio")
    @Size(max = 20, message = "El CI no puede superar 20 caracteres")
    private String ci;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String correo;
}
