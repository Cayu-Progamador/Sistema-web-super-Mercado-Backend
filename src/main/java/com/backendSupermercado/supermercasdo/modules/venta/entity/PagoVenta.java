package com.backendSupermercado.supermercasdo.modules.venta.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pago_venta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_venta")
    private Long idPagoVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Column(name = "monto_efectivo", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montoEfectivo = BigDecimal.ZERO;

    @Column(name = "monto_qr", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal montoQr = BigDecimal.ZERO;

    @Column(name = "total_recibido", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalRecibido;

    @Column(name = "cambio", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal cambio = BigDecimal.ZERO;

    @Column(name = "referencia_qr", length = 100)
    private String referenciaQr;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
}
