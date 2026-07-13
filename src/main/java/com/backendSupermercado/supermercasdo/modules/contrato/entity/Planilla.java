package com.backendSupermercado.supermercasdo.modules.contrato.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "planilla",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_planilla_contrato_periodo", columnNames = {"id_contrato", "anio", "mes"})
    }
)
public class Planilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Integer mes;

    @Column(name = "sueldo_base", precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Column(name = "total_bonos", precision = 10, scale = 2)
    private BigDecimal totalBonos;

    @Column(name = "total_descuentos", precision = 10, scale = 2)
    private BigDecimal totalDescuentos;

    @Column(name = "total_pagar", precision = 10, scale = 2)
    private BigDecimal totalPagar;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato", nullable = false)
    private Contrato contrato;
}
