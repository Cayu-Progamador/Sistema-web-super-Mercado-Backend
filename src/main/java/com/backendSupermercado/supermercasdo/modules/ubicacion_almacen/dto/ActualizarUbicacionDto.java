package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActualizarUbicacionDto {
    @NotBlank(message="El nombre de la ubicación es obligatorio") @Size(max=100)
    private String nombre;
    @Size(max=30) private String tipo;
    @Size(max=255) private String descripcion;
}
