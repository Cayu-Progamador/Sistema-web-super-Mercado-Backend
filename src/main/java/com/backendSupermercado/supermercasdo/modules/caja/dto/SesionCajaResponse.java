package com.backendSupermercado.supermercasdo.modules.caja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoSesionCaja;

public record SesionCajaResponse(Long idSesionCaja, Long idCaja, String caja, Long idUsuario, String cajero, BigDecimal montoInicial,
        BigDecimal montoEsperado, BigDecimal efectivoContado, BigDecimal diferencia, BigDecimal efectivo, BigDecimal qr,
        BigDecimal mixto, BigDecimal mixtoEfectivo, BigDecimal mixtoQr, BigDecimal credito, BigDecimal pagosCredito,
        BigDecimal creditoPendiente, BigDecimal creditoEfectivo, BigDecimal creditoQr, LocalDateTime fechaApertura,
        LocalDateTime fechaCierre, EstadoSesionCaja estado, String observacion) {
    public String usuario() {
        return cajero;
    }
}
