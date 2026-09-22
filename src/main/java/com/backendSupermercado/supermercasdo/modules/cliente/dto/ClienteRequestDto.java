package com.backendSupermercado.supermercasdo.modules.cliente.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Size(max = 100, message = "El apellido paterno no puede superar 100 caracteres")
    private String apellidoPaterno;

    @Size(max = 100, message = "El apellido materno no puede superar 100 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "El CI es obligatorio")
    @Size(min = 7, max = 8, message = "El CI debe tener entre 7 y 8 dígitos")
    @Pattern(regexp = "\\d{7,8}", message = "El CI debe contener únicamente entre 7 y 8 dígitos")
    private String ci;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String correo;

    @Size(max = 20, message = "El celular no puede superar 20 caracteres")
    @Pattern(regexp = "^$|\\+?[0-9][0-9\\s-]{6,19}$", message = "El celular no tiene un formato válido")
    private String telefono;

    @DecimalMin(value = "0.00", message = "El límite de crédito no puede ser negativo")
    private BigDecimal limiteCredito;

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    private String direccion;
}
