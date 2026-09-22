package com.backendSupermercado.supermercasdo.modules.caja.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoSesionCaja;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.venta.entity.Venta;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sesion_caja")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion_caja")
    private Long idSesionCaja;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @Column(name = "monto_inicial", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoInicial;
    @Column(name = "monto_esperado", precision = 14, scale = 2)
    private BigDecimal montoEsperado;
    @Column(name = "efectivo_contado", precision = 14, scale = 2)
    private BigDecimal efectivoContado;
    @Column(precision = 14, scale = 2)
    private BigDecimal diferencia;
    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoSesionCaja estado = EstadoSesionCaja.ABIERTA;
    @Column(columnDefinition = "TEXT")
    private String observacion;
    @OneToMany(mappedBy = "sesionCaja", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Venta> ventas = new ArrayList<>();

    @PrePersist
    void alCrear() {
        if (fechaApertura == null)
            fechaApertura = LocalDateTime.now();
        if (montoEsperado == null)
            montoEsperado = montoInicial;
    }
}
