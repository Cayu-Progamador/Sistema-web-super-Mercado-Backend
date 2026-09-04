package com.backendSupermercado.supermercasdo.modules.vencimientos.model;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public enum VencimientoEstado {
    VENCIDO,
    VENCE_HOY,
    URGENTE,
    PROXIMO,
    NORMAL;

    public static VencimientoEstado calcular(LocalDate fechaVencimiento, LocalDate hoy) {
        long dias = ChronoUnit.DAYS.between(hoy, fechaVencimiento);

        if (dias < 0) return VENCIDO;
        if (dias == 0) return VENCE_HOY;
        if (dias <= 7) return URGENTE;
        if (dias <= 30) return PROXIMO;
        return NORMAL;
    }

    public static VencimientoEstado desdeTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String normalizado = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        try {
            return valueOf(normalizado);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Estado de vencimiento inválido. Valores permitidos: "
                            + "VENCIDO, VENCE_HOY, URGENTE, PROXIMO y NORMAL");
        }
    }

    public String etiqueta() {
        return this == PROXIMO ? "PRÓXIMO" : name();
    }
}
