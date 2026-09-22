package com.backendSupermercado.supermercasdo.modules.credito.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.venta.entity.CuentaPorCobrar;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;
import com.backendSupermercado.supermercasdo.modules.caja.entity.SesionCaja;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pago_credito")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_credito")
    private Long idPagoCredito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cuenta_por_cobrar", nullable = false)
    private CuentaPorCobrar cuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sesion_caja", nullable = false)
    private SesionCaja sesionCaja;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal monto;

    @Column(name = "monto_efectivo", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montoEfectivo = BigDecimal.ZERO;

    @Column(name = "monto_qr", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montoQr = BigDecimal.ZERO;

    @Column(name = "referencia_qr", length = 100)
    private String referenciaQr;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
}
