package com.backendSupermercado.supermercasdo.modules.lotes.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record LoteUpdateRequestDto(@NotBlank(message="El código de lote es obligatorio") @Size(max=50,message="El código no puede superar 50 caracteres") String codigoLote, LocalDate fechaFabricacion, LocalDate fechaVencimiento, @Size(max=255,message="La observación no puede superar 255 caracteres") String observacion) { }
