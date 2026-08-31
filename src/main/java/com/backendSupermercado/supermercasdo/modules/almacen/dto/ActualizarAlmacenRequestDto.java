package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarAlmacenRequestDto {
    @NotBlank(message = "El nombre del almacén es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de almacén es obligatorio")
    @Positive(message = "El tipo de almacén no es válido")
    private Long idTipoAlmacen;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @Size(max = 255, message = "La dirección o referencia no puede superar 255 caracteres")
    private String direccion;

    @Positive(message = "El responsable seleccionado no es válido")
    private Long idResponsable;
}
