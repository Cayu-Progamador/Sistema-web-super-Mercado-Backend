package com.backendSupermercado.supermercasdo.shared.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FechaUtil {

    public static LocalDate hoy() {
        return LocalDate.now();
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now();
    }

    public static String formatoFechea(LocalDate fecha) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formato);
    }
}
