package com.backendSupermercado.supermercasdo.modules.cliente.dto;

import java.time.LocalDateTime;

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
public class ClienteResponseDto {

    private Long idCliente;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String ci;
    private String correo;
    private Boolean estado;
    private LocalDateTime fechaRegistro;
}
