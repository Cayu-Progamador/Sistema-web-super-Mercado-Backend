package com.backendSupermercado.supermercasdo.modules.pedidos.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDto {

    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    private LocalDate fechaEntregaEstimada;

    @Size(max = 1000, message = "La observación no puede exceder 1000 caracteres")
    private String observacion;

    @NotBlank(message = "El estado es obligatorio")
    @Size(min = 3, max = 30, message = "El estado debe tener entre 3 y 30 caracteres")
    private String estado;

    @Valid
    @NotNull(message = "Debe incluir al menos un detalle de pedido")
    private List<PedidoDetalleRequestDto> detalles = new ArrayList<>();
}
